package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.EntryCreators;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.wrupple.muba.catalogs.domain.CatalogEvent.CREATE_ACTION;

@Singleton
public class CatalogCreateTransactionImpl extends CatalogTransaction implements CatalogCreateTransaction {
	protected static final Logger log = LoggerFactory.getLogger(CatalogCreateTransactionImpl.class);

	//private final CatalogActionTriggerHandler trigerer;
	private final FieldAccessStrategy access;
	private final EntryCreators creators;
	private final boolean follow;
    private final EntrySynthesizer delegate;

    @Inject
	public CatalogCreateTransactionImpl(@Named("catalog.followGraph") Boolean follow, EntryCreators creators, CatalogFactory factory, String creatorsDictionary, Provider<CatalogActionCommit> catalogActionCommitProvider, FieldAccessStrategy access, EntrySynthesizer delegate) {
        super(catalogActionCommitProvider);
        this.creators=creators;
		this.follow=follow==null?false:follow.booleanValue();
		this.access = access;
        this.delegate = delegate;
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

		Instrospection instrospection = access.newSession(result);
		
		log.trace("[catalog/storage] {}/{}",catalog.getDistinguishedName(),createDao.getClass());
		if(follow||context.getRequest().getFollowReferences()){
			Collection<FieldDescriptor> fields = catalog.getFieldsValues();
			for(FieldDescriptor field: fields){
				if (field.isKey() && access.getPropertyValue(field, result, null, instrospection) == null) {
					Object foreignValue = delegate.getPropertyForeignKeyValue(catalog, field, result, instrospection);
					if(foreignValue!=null){
						//if we got to this point, force the context to follow the reference graph


						 createRefereces(context,catalog,field,foreignValue,result, instrospection);
					}
				}
			}
		}

		
		CatalogEntry parentEntry = null;
		String greatAncestor = delegate.evaluateGreatAncestor(context,catalog,null);
		if (  greatAncestor!= null && !catalog.getConsolidated()) {

			parentEntry= create( result, instrospection, catalog, context,context);
		}
		
		createDao.execute(context);
		
		CatalogEntry regreso = context.getEntryResult();
		
		if(regreso!=null){
			if (parentEntry!=null &&greatAncestor != null && !catalog.getConsolidated() ) {
                delegate.addInheritedValuesToChild(parentEntry,  regreso, instrospection,catalog);
			}
			context.getRuntimeContext().getTransactionHistory().didCreate(context, regreso, createDao);
		}
		
		//local cache FIXME only empty lists when storage is not guarantee sequential insertion
		CatalogResultCache cache = context.getCache(catalog, context);
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
		Object allegedParentId = delegate.getAllegedParentId(result, instrospection,access);
		CatalogDescriptor parentCatalog = childContext.getDescriptorForKey(parentCatalogId);
		CatalogEntry parentEntity = createAncestorsRecursively(result, parentCatalog, allegedParentId, instrospection,childContext);
		Object parentEntityId = parentEntity.getId();
		CatalogEntry childEntity = delegate.synthesizeChildEntity(parentEntityId, result, instrospection, catalog,childContext);
		
		parentContext.getRequest().setEntryValue(childEntity);
		return parentEntity;
	}
	
	private CatalogEntry createAncestorsRecursively(CatalogEntry o, CatalogDescriptor parentCatalog, Object allegedParentId, Instrospection instrospection,
			CatalogActionContext context) throws Exception {

		// synthesize parent entity from all non-inherited, passing all
		// inherited field Values

		CatalogEntry parentEntity;
		if (allegedParentId == null) {
			parentEntity = delegate.synthesizeCatalogObject(o, parentCatalog, false, instrospection, context);

			parentEntity = context.triggerCreate(parentCatalog.getDistinguishedName(),parentEntity);
		} else {

			parentEntity =context.triggerGet(parentCatalog.getDistinguishedName(),allegedParentId);
			if (parentEntity == null) {
				throw new IllegalArgumentException("entry parent does not exist " + allegedParentId + "@" + parentCatalog.getDistinguishedName());
			}
		}
		return parentEntity;
	}


	void createRefereces(CatalogActionContext context, CatalogDescriptor catalog, FieldDescriptor field,
								Object foreignValue,CatalogEntry owner, Instrospection instrospection) throws Exception {
		String reservedField;
		if (field.isMultiple()) {
			Collection<CatalogEntry> entries = (Collection<CatalogEntry>) foreignValue;
			List<CatalogEntry> createdValues = new ArrayList<CatalogEntry>(entries.size());
			for (CatalogEntry entry : entries) {
				if (entry.getId() == null) {
					createdValues.add(context.triggerCreate(field.getCatalog(), entry));
				} else {
					createdValues.add(entry);
				}
			}

			reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
			if (access.isWriteableProperty(reservedField, owner, instrospection)) {
				access.setPropertyValue(reservedField, owner, createdValues, instrospection);
			}
			List<Object> keys = createdValues.stream().map(v -> v.getId()).collect(Collectors.toList());

			access.setPropertyValue(field, owner, keys, instrospection);
		} else {
			// FIXME this is not batch at all, must be able to load a context with
			// multile input entries, just like there is multiple results by default

			CatalogEntry value =  context.triggerCreate(field.getCatalog(), (CatalogEntry) foreignValue);
			reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
			if (access.isWriteableProperty(reservedField, owner, instrospection)) {
				access.setPropertyValue(reservedField, owner, value, instrospection);
			}
			access.setPropertyValue(field, owner, value.getId(), instrospection);
		}
	}

}
