package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
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
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

import static com.wrupple.muba.catalogs.domain.CatalogContract.CREATE_ACTION;

@Singleton
public class CatalogCreateTransactionImpl extends CatalogTransaction implements CatalogCreateTransaction {
	protected static final Logger log = LogManager.getLogger(CatalogCreateTransactionImpl.class);

	//private final CatalogActionTriggerHandler trigerer;
	private final FieldAccessStrategy access;
	private final EntryCreators creators;
	private final boolean follow;
	private final CompleteCatalogGraph graphJoin;
	private final EntrySynthesizer delegate;

    @Inject
	public CatalogCreateTransactionImpl(@Named("catalog.followGraph") Boolean follow, EntryCreators creators, CatalogFactory factory, String creatorsDictionary, Provider<CatalogActionCommit> catalogActionCommitProvider, FieldAccessStrategy access, CompleteCatalogGraph graphJoin, EntrySynthesizer delegate) {
        super(catalogActionCommitProvider);
        this.creators=creators;
		this.follow=follow==null?false:follow.booleanValue();
		this.access = access;
		this.graphJoin = graphJoin;
		this.delegate = delegate;
    }

	
	@Override
	public boolean execute(Context cxt) throws Exception {

		CatalogActionContext context = (CatalogActionContext) cxt;

		CatalogEntry result = (CatalogEntry) context.getRequest().getEntryValue();
		willBeCreated(context,  result);
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
			Object foreignValue;
			for(FieldDescriptor field: fields){
				if (field.isKey()) {
                    foreignValue=null;

                    if(access.getPropertyValue(field, result, null, instrospection) == null){
                        foreignValue = delegate.getPropertyForeignKeyValue(catalog, field, result, instrospection);
                    }
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

			parentEntry= create( result, instrospection, catalog, context);
		}

		createDao.execute(context);


		CatalogEntry regreso = context.getEntryResult();

        wasCreated(context,result);

        if (follow||context.getRequest().getFollowReferences()) {// interceptor
            context.setCatalogDescriptor(catalog);
            context.getRequest().setFilter(null);
            context.getRequest().setEntry(context.getEntryResult().getId());
            graphJoin.execute(context);
        }


        if(regreso!=null){
			if (parentEntry!=null &&greatAncestor != null && !catalog.getConsolidated() ) {
                delegate.addInheritedValuesToChild(parentEntry,  regreso, instrospection,catalog);
			}
			context.getRuntimeContext().getTransactionHistory().didCreate(context, regreso, createDao);
		}

		CatalogResultCache cache = context.getCache(catalog, context);
		if (cache != null) {
			if(!createDao.isSequential()){
                cache.clearLists(context,catalog.getDistinguishedName());
            }
		}

        log.debug("<CatalogActionEvent-Broadcast>");
        postProcess(context,catalog.getDistinguishedName(),CREATE_ACTION,regreso);
        log.debug("</CatalogActionEvent-Broadcast>");

        context.setResults(Collections.singletonList(regreso));

		return CONTINUE_PROCESSING;
	}

	
	private CatalogEntry create(CatalogEntry result, Instrospection instrospection, CatalogDescriptor catalog, CatalogActionContext parentContext) throws Exception {
		Long parentCatalogId = catalog.getParent();
		Object allegedParentId = delegate.getAllegedParentId(result, instrospection,access);
		CatalogDescriptor parentCatalog = parentContext.getDescriptorForKey(parentCatalogId);
		CatalogEntry parentEntity = createAncestorsRecursively(result, parentCatalog, allegedParentId, instrospection,parentContext);

        delegate.addInheritedValuesToChild(parentEntity,result,instrospection,catalog);
		
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
            CatalogEntry created;
            boolean alterationsMade = false;
			for (CatalogEntry entry : entries) {
				if (entry.getId() == null) {
                    if(beeingCreated(context,entry)){
                        queueCreationCallback(context,entry,new MultipleBackReferencePropagation(field,owner,instrospection,catalog));
                    }else{
                        created= context.triggerCreate(field.getCatalog(), entry);

                        createdValues.add(created);
                        alterationsMade=true;
                    }

				} else {
					createdValues.add(entry);
				}
			}

			if(alterationsMade){
                reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
                if (access.isWriteableProperty(reservedField, owner, instrospection)) {
                    access.setPropertyValue(reservedField, owner, createdValues, instrospection);
                }
                List<Object> keys = createdValues.stream().map(v -> v.getId()).filter(v -> v!=null).collect(Collectors.toList());

                if(keys.isEmpty()){
                    access.setPropertyValue(field, owner, null, instrospection);

                }else{
                    access.setPropertyValue(field, owner, keys, instrospection);
                }
            }
		} else {
            CatalogEntry entry = (CatalogEntry) foreignValue;
            if (entry.getId() == null) {
                if(beeingCreated(context,entry)){
                    queueCreationCallback(context,entry,new SingleBackReferencePropagation(field,owner,catalog,instrospection));
                }else{
                    entry =  context.triggerCreate(field.getCatalog(), entry);
                    reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
                    if (access.isWriteableProperty(reservedField, owner, instrospection)) {
                        access.setPropertyValue(reservedField, owner, entry, instrospection);
                    }
                }

            }
            access.setPropertyValue(field, owner, entry.getId(), instrospection);

		}
	}


    private class MultipleBackReferencePropagation implements Command<CatalogActionContext>{

        final FieldDescriptor field;
        final CatalogEntry owner;
        final Instrospection instrospection;
        final CatalogDescriptor catalog;

        private MultipleBackReferencePropagation(FieldDescriptor field, CatalogEntry owner, Instrospection instrospection, CatalogDescriptor catalog) {
            this.field = field;
            this.owner = owner;
            this.instrospection = instrospection;
            this.catalog = catalog;
        }

        @Override
        public synchronized boolean  execute(CatalogActionContext context) throws Exception {

            Collection<CatalogEntry> createdValues = (Collection) delegate.getPropertyForeignKeyValue(catalog, field, owner, instrospection);

            createdValues.add(context.getResult());
            String reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
            if (access.isWriteableProperty(reservedField, owner, instrospection)) {
                access.setPropertyValue(reservedField, owner, createdValues, instrospection);
            }
            List<Object> keys = createdValues.stream().map(v -> v.getId()).filter(v -> v!=null).collect(Collectors.toList());

            if(keys.isEmpty()){
                access.setPropertyValue(field, owner, null, instrospection);

            }else{
                access.setPropertyValue(field, owner, keys, instrospection);
            }
            FieldDescriptor reservedDescriptor = catalog.getFieldDescriptor(reservedField);
            if(reservedDescriptor!=null && reservedDescriptor.isWriteable() &&!reservedDescriptor.isEphemeral()){
                context.triggerWrite(catalog.getDistinguishedName(),owner.getId(),owner);
            }
            return CONTINUE_PROCESSING;
        }
    }


    private class SingleBackReferencePropagation implements Command<CatalogActionContext>{

        final FieldDescriptor field;
        final CatalogEntry owner;
        final Instrospection instrospection;
        private final CatalogDescriptor owenerCatalog;

        private SingleBackReferencePropagation(FieldDescriptor field, CatalogEntry owner,CatalogDescriptor owenerCatalog, Instrospection instrospection) {
            this.field = field;
            this.owner = owner;
            this.owenerCatalog=owenerCatalog;
            this.instrospection = instrospection;
        }

        @Override
        public boolean execute(CatalogActionContext context) throws Exception {
            CatalogEntry entry = context.getResult();
            String reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
            if (access.isWriteableProperty(reservedField, owner, instrospection)) {
                access.setPropertyValue(reservedField, owner, entry, instrospection);
            }
            access.setPropertyValue(field, owner, entry.getId(), instrospection);
            FieldDescriptor reservedDescriptor = owenerCatalog.getFieldDescriptor(reservedField);
            if(reservedDescriptor!=null && reservedDescriptor.isWriteable() &&!reservedDescriptor.isEphemeral()){
                context.triggerWrite(owenerCatalog.getDistinguishedName(),owner.getId(),owner);

            }

            return CONTINUE_PROCESSING;
        }
    }

    private void willBeCreated(CatalogActionContext context, CatalogEntry entry) {
        IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>>  set = assertSet(context);
        set.put(entry,new ArrayList<>(2));
    }

    private static final String PROPERTY = "com.wrupple.catalog.creationGraph";

    private IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>> assertSet(CatalogActionContext c) {
        RuntimeContext context = c.getRuntimeContext().getRootAncestor();
        IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>> set = (IdentityHashMap) context.get(PROPERTY);
        if(set==null){
            set = new IdentityHashMap<>();
            context.put(PROPERTY,set);
        }
        return  set;
    }

    private boolean beeingCreated(CatalogActionContext context, CatalogEntry entry) {
        IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>> set = assertSet(context);
        return set.containsKey(entry);
    }


    private void queueCreationCallback(
            CatalogActionContext context,
            CatalogEntry entry,
            Command<CatalogActionContext> callback) {
        IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>> set = assertSet(context);
        List<Command<CatalogActionContext>> callbacks = set.get(entry);
        callbacks.add(callback);
        
    }

    private void wasCreated(CatalogActionContext context, CatalogEntry entry) throws Exception {
        IdentityHashMap<CatalogEntry,List<Command<CatalogActionContext>>> set = assertSet(context);

        List<Command<CatalogActionContext>> callbacks =set.remove(entry);
        for(Command<CatalogActionContext> callback:  callbacks){
            callback.execute(context);
        }
    }

}