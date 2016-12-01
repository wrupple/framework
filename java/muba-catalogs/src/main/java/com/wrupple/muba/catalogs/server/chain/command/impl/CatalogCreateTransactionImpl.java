package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogChangeEvent;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.domain.CatalogChangeEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.EntryCreators;

@Singleton
public class CatalogCreateTransactionImpl implements CatalogCreateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogCreateTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;
	private final EntryCreators creators;
	private final CatalogEvaluationDelegate accessor;
	private final boolean CREATE_RECURSIVE;

	@Inject
	public CatalogCreateTransactionImpl(EntryCreators creators,CatalogActionTriggerHandler trigerer,CatalogFactory factory, String creatorsDictionary, CatalogEvaluationDelegate accessor,@Named("catalog.create.recursive") Boolean recursive) {
		this.trigerer=trigerer;
		this.creators=creators;
		this.accessor=accessor;
		this.CREATE_RECURSIVE=recursive;
	}

	
	@Override
	public boolean execute(Context cxt) throws Exception {
		log.trace("[CREATE START]");
		CatalogActionContext context = (CatalogActionContext) cxt;

		// must have been deserialized by this point
		CatalogEntry result = (CatalogEntry) context.getEntryValue();
		assert result != null : "no data";
		context.setAction(CatalogActionRequest.CREATE_ACTION);
		trigerer.execute(context);
		
		CatalogDescriptor catalog=context.getCatalogDescriptor();
		DataCreationCommand createDao = (DataCreationCommand) creators.getCommand(String.valueOf(catalog.getStorage()));

		Session session = accessor.newSession(result);
		
		log.trace("[catalog/storage] {}/{}",catalog.getDistinguishedName(),createDao.getClass());
		if(this.CREATE_RECURSIVE){
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			for(FieldDescriptor field: fields){
				if(field.isKey() && accessor.getPropertyValue(catalog, field, result, null, session)==null){
					Object foreignValue = accessor.getPropertyForeignKeyValue(catalog, field, result, session);
					if(foreignValue!=null){
						CatalogActionContext recursiveCreationContext  = context.getCatalogManager().spawn(context);
						recursiveCreationContext.setCatalog(field.getCatalog());
						foreignValue= recursiveCreationContext.getCatalogManager().createBatch(recursiveCreationContext,catalog,field,foreignValue);
						accessor.setPropertyValue(catalog, field, result, foreignValue, session);
					}
				}
			}
		}
		
		
		CatalogEntry parentEntry = null;
		if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
			CatalogActionContext ancestorsCreationContext  = context.getCatalogManager().spawn(context);

			parentEntry= create( result, session, catalog, ancestorsCreationContext,context);
		}
		
		createDao.execute(context);
		
		CatalogEntry regreso = context.getEntryResult();
		
		if(regreso!=null){
			if (parentEntry!=null &&catalog.getGreatAncestor() != null && !catalog.isConsolidated() ) {
				accessor.addInheritedValuesToChild(parentEntry,  regreso, session,catalog);
			}
			context.getTransactionHistory().didCreate(context, regreso, createDao);
		}
		
		//local cache
		CatalogResultCache cache = context.getCatalogManager().getCache(catalog, context);
		if (cache != null) {
			cache.clearLists(context,catalog.getDistinguishedName());
		}
		
		
		CatalogChangeEvent event=new CatalogChangeEventImpl((Long) context.getDomain(), catalog.getDistinguishedName(), CatalogActionRequest.CREATE_ACTION, regreso);
		//cache invalidation
		context.getRootAncestor().addBroadcastable(event);
		
		
		trigerer.postprocess(context, context.getExcecutionContext().getCaughtException());
		context.setResults(Collections.singletonList(regreso));
		log.trace("[END] created: {}", regreso);
		return CONTINUE_PROCESSING;
	}

	
	private CatalogEntry create(CatalogEntry result, Session session,CatalogDescriptor catalog,CatalogActionContext childContext, CatalogActionContext parentContext) throws Exception {
		Long parentCatalogId = catalog.getParent();
		Object allegedParentId = accessor.getAllegedParentId(result,session);
		CatalogDescriptor parentCatalog = childContext.getCatalogManager().getDescriptorForKey(parentCatalogId, childContext);
		CatalogEntry parentEntity = createAncestorsRecursively(result, parentCatalog, allegedParentId, session,childContext);
		Object parentEntityId = parentEntity.getId();
		CatalogEntry childEntity = accessor.synthesizeChildEntity(parentEntityId, result, session, catalog,childContext);
		
		parentContext.setEntryValue(childEntity);
		return parentEntity;
	}
	
	private CatalogEntry createAncestorsRecursively(CatalogEntry o, CatalogDescriptor parentCatalog, Object allegedParentId, Session session,
			CatalogActionContext childContext) throws Exception {

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values

		CatalogEntry parentEntity;
		if (allegedParentId == null) {
			parentEntity = accessor.synthesizeCatalogObject(o, parentCatalog, false, session, childContext);
			childContext.setEntryValue(parentEntity);
			childContext.setCatalogDescriptor(parentCatalog);
			childContext.getCatalogManager().getNew().execute(childContext);
			parentEntity = childContext.getEntryResult();
		} else {
			parentEntity = accessor.readEntry(parentCatalog, allegedParentId, childContext);
			if (parentEntity == null) {
				throw new IllegalArgumentException("entry parent does not exist " + allegedParentId + "@" + parentCatalog.getDistinguishedName());
			}
		}
		return parentEntity;
	}
	



}
