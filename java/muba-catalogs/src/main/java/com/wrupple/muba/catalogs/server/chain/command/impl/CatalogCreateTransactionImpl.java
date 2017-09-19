package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogChangeEvent;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.domain.CatalogChangeEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.event.domain.Instrospector;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;

@Singleton
public class CatalogCreateTransactionImpl implements CatalogCreateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogCreateTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;
	private final EntryCreators creators;
	private final boolean follow;

	@Inject
	public CatalogCreateTransactionImpl(@Named("catalog.followGraph")Boolean follow,EntryCreators creators, CatalogActionTriggerHandler trigerer, CatalogFactory factory, String creatorsDictionary) {
		this.trigerer=trigerer;
		this.creators=creators;
		this.follow=follow==null?false:follow.booleanValue();
	}

	
	@Override
	public boolean execute(Context cxt) throws Exception {
		log.trace("[CREATE START]");
		CatalogActionContext context = (CatalogActionContext) cxt;

		// must have been deserialized by this point
		CatalogEntry result = (CatalogEntry) context.getEntryValue();
		if(result ==null){
			throw new NullPointerException("no entry in context");
		}
		context.setName(CatalogActionRequest.CREATE_ACTION);
		trigerer.execute(context);
		
		CatalogDescriptor catalog=context.getCatalogDescriptor();
		DataCreationCommand createDao = (DataCreationCommand) creators.getCommand(String.valueOf(catalog.getStorage()));

		Instrospector instrospector = context.getCatalogManager().access().newSession(result);
		
		log.trace("[catalog/storage] {}/{}",catalog.getDistinguishedName(),createDao.getClass());
		if(follow||context.getFollowReferences()){
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			for(FieldDescriptor field: fields){
				if (field.isKey() && context.getCatalogManager().access().getPropertyValue(field, result, null, instrospector) == null) {
					Object foreignValue = context.getCatalogManager().getPropertyForeignKeyValue(catalog, field, result, instrospector);
					if(foreignValue!=null){
						//if we got to this point, force the context to follow the reference graph
						context.setFollowReferences(true);
						CatalogActionContext recursiveCreationContext  = context.getCatalogManager().spawn(context);
						recursiveCreationContext.setFollowReferences(context.getFollowReferences());
						recursiveCreationContext.setCatalog(field.getCatalog());
						 recursiveCreationContext.getCatalogManager().createRefereces(recursiveCreationContext,catalog,field,foreignValue,result, instrospector);
					}
				}
			}
		}
		
		
		CatalogEntry parentEntry = null;
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			CatalogActionContext ancestorsCreationContext  = context.getCatalogManager().spawn(context);

			parentEntry= create( result, instrospector, catalog, ancestorsCreationContext,context);
		}
		
		createDao.execute(context);
		
		CatalogEntry regreso = context.getEntryResult();
		
		if(regreso!=null){
			if (parentEntry!=null &&catalog.getGreatAncestor() != null && !catalog.isConsolidated() ) {
				context.getCatalogManager().addInheritedValuesToChild(parentEntry,  regreso, instrospector,catalog);
			}
			context.getTransactionHistory().didCreate(context, regreso, createDao);
		}
		
		//local cache FIXME only empty lists when storage is not guarantee sequential insertion
		CatalogResultCache cache = context.getCatalogManager().getCache(catalog, context);
		if (cache != null) {
			cache.clearLists(context,catalog.getDistinguishedName());
		}
		
		
		CatalogChangeEvent event=new CatalogChangeEventImpl((Long) context.getDomain(), catalog.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, regreso);
		//cache invalidation
		context.getRootAncestor().addBroadcastable(event);
		
		
		trigerer.postprocess(context, context.getRuntimeContext().getCaughtException());
		context.setResults(Collections.singletonList(regreso));
		log.trace("[END] created: {}", regreso);
		return CONTINUE_PROCESSING;
	}

	
	private CatalogEntry create(CatalogEntry result, Instrospector instrospector, CatalogDescriptor catalog, CatalogActionContext childContext, CatalogActionContext parentContext) throws Exception {
		Long parentCatalogId = catalog.getParent();
		Object allegedParentId = childContext.getCatalogManager().getAllegedParentId(result, instrospector);
		CatalogDescriptor parentCatalog = childContext.getCatalogManager().getDescriptorForKey(parentCatalogId, childContext);
		CatalogEntry parentEntity = createAncestorsRecursively(result, parentCatalog, allegedParentId, instrospector,childContext);
		Object parentEntityId = parentEntity.getId();
		CatalogEntry childEntity = childContext.getCatalogManager().synthesizeChildEntity(parentEntityId, result, instrospector, catalog,childContext);
		
		parentContext.setEntryValue(childEntity);
		return parentEntity;
	}
	
	private CatalogEntry createAncestorsRecursively(CatalogEntry o, CatalogDescriptor parentCatalog, Object allegedParentId, Instrospector instrospector,
			CatalogActionContext childContext) throws Exception {

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values

		CatalogEntry parentEntity;
		if (allegedParentId == null) {
			parentEntity = childContext.getCatalogManager().synthesizeCatalogObject(o, parentCatalog, false, instrospector, childContext);
			childContext.setEntryValue(parentEntity);
			childContext.setCatalogDescriptor(parentCatalog);
			childContext.getCatalogManager().getNew().execute(childContext);
			parentEntity = childContext.getEntryResult();
		} else {
			parentEntity = childContext.getCatalogManager().readEntry(parentCatalog, allegedParentId, childContext);
			if (parentEntity == null) {
				throw new IllegalArgumentException("entry parent does not exist " + allegedParentId + "@" + parentCatalog.getDistinguishedName());
			}
		}
		return parentEntity;
	}
	



}
