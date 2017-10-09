package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.event.domain.Instrospection;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;

import static com.wrupple.muba.catalogs.domain.CatalogEvent.CREATE_ACTION;

@Singleton
public class CatalogCreateTransactionImpl extends CatalogTransaction implements CatalogCreateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogCreateTransactionImpl.class);

	//private final CatalogActionTriggerHandler trigerer;
	private final EntryCreators creators;
	private final boolean follow;

	@Inject
	public CatalogCreateTransactionImpl(@Named("catalog.followGraph") Boolean follow, EntryCreators creators, CatalogFactory factory, String creatorsDictionary, Provider<CatalogActionCommit> catalogActionCommitProvider) {
        super(catalogActionCommitProvider);
        this.creators=creators;
		this.follow=follow==null?false:follow.booleanValue();
	}

	
	@Override
	public boolean execute(Context cxt) throws Exception {
		log.trace("<{}>",this.getClass().getSimpleName());
		CatalogActionContext context = (CatalogActionContext) cxt;

		// must have been deserialized by this point
		CatalogEntry result = (CatalogEntry) context.getRequest().getEntryValue();
		if(result ==null){
			throw new NullPointerException("no entry in context");
		}
        log.debug("<CatalogActionFilter>");
        preprocess(context,CREATE_ACTION);
        log.debug("</CatalogActionFilter>");

        CatalogDescriptor catalog=context.getCatalogDescriptor();
		DataCreationCommand createDao = (DataCreationCommand) creators.getCommand(String.valueOf(catalog.getStorage()));

		Instrospection instrospection = context.getCatalogManager().access().newSession(result);
		
		log.trace("[catalog/storage] {}/{}",catalog.getDistinguishedName(),createDao.getClass());
		if(follow||context.getRequest().getFollowReferences()){
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			for(FieldDescriptor field: fields){
				if (field.isKey() && context.getCatalogManager().access().getPropertyValue(field, result, null, instrospection) == null) {
					Object foreignValue = context.getCatalogManager().getPropertyForeignKeyValue(catalog, field, result, instrospection);
					if(foreignValue!=null){
						//if we got to this point, force the context to follow the reference graph


						 context.getCatalogManager().createRefereces(context,catalog,field,foreignValue,result, instrospection);
					}
				}
			}
		}
		
		
		CatalogEntry parentEntry = null;
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {

			parentEntry= create( result, instrospection, catalog, context,context);
		}
		
		createDao.execute(context);
		
		CatalogEntry regreso = context.getEntryResult();
		
		if(regreso!=null){
			if (parentEntry!=null &&catalog.getGreatAncestor() != null && !catalog.isConsolidated() ) {
				context.getCatalogManager().addInheritedValuesToChild(parentEntry,  regreso, instrospection,catalog);
			}
			context.getRuntimeContext().getTransactionHistory().didCreate(context, regreso, createDao);
		}
		
		//local cache FIXME only empty lists when storage is not guarantee sequential insertion
		CatalogResultCache cache = context.getCatalogManager().getCache(catalog, context);
		if (cache != null) {
			cache.clearLists(context,catalog.getDistinguishedName());
		}


        log.debug("<CatalogActionEvent-Broadcast>");
        postProcess(context,catalog.getDistinguishedName(),CREATE_ACTION,regreso);
        log.debug("</CatalogActionEvent-Broadcast>");

		context.setResults(Collections.singletonList(regreso));
		log.trace("</{}>",this.getClass().getSimpleName());
		return CONTINUE_PROCESSING;
	}

	
	private CatalogEntry create(CatalogEntry result, Instrospection instrospection, CatalogDescriptor catalog, CatalogActionContext childContext, CatalogActionContext parentContext) throws Exception {
		Long parentCatalogId = catalog.getParent();
		Object allegedParentId = childContext.getCatalogManager().getAllegedParentId(result, instrospection);
		CatalogDescriptor parentCatalog = childContext.getCatalogManager().getDescriptorForKey(parentCatalogId, childContext);
		CatalogEntry parentEntity = createAncestorsRecursively(result, parentCatalog, allegedParentId, instrospection,childContext);
		Object parentEntityId = parentEntity.getId();
		CatalogEntry childEntity = childContext.getCatalogManager().synthesizeChildEntity(parentEntityId, result, instrospection, catalog,childContext);
		
		parentContext.getRequest().setEntryValue(childEntity);
		return parentEntity;
	}
	
	private CatalogEntry createAncestorsRecursively(CatalogEntry o, CatalogDescriptor parentCatalog, Object allegedParentId, Instrospection instrospection,
			CatalogActionContext context) throws Exception {

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values

		CatalogEntry parentEntity;
		if (allegedParentId == null) {
			parentEntity = context.getCatalogManager().synthesizeCatalogObject(o, parentCatalog, false, instrospection, context);

			parentEntity = context.triggerCreate(parentCatalog.getDistinguishedName(),parentEntity);
		} else {

			parentEntity =context.triggerGet(parentCatalog.getDistinguishedName(),allegedParentId);
			if (parentEntity == null) {
				throw new IllegalArgumentException("entry parent does not exist " + allegedParentId + "@" + parentCatalog.getDistinguishedName());
			}
		}
		return parentEntity;
	}
	



}
