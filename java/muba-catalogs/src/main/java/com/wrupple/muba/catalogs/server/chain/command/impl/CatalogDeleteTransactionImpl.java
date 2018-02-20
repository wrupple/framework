package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.wrupple.muba.catalogs.domain.CatalogContract.DELETE_ACTION;

@Singleton
public class CatalogDeleteTransactionImpl extends CatalogTransaction implements CatalogDeleteTransaction {

	protected static final Logger log = LoggerFactory.getLogger(CatalogDeleteTransactionImpl.class);



    private final FieldAccessStrategy access;
	private final Deleters deleters;
	private final EntrySynthesizer synthesizer;
	private final ActionsDictionary dictionary;

	@Inject
	public CatalogDeleteTransactionImpl(Provider<CatalogActionCommit> catalogActionCommitProvider, FieldAccessStrategy access, Deleters deleters, EntrySynthesizer synthesizer, ActionsDictionary dictionary) {
		super(catalogActionCommitProvider);
        this.access = access;
        this.synthesizer = synthesizer;
        this.dictionary=dictionary;
		this.deleters = deleters;
	}

	@Override
	public boolean execute(Context c) throws Exception {

		CatalogActionContext context = (CatalogActionContext) c;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		FieldDescriptor trashableField = catalog.getFieldDescriptor(Trash.TRASH_FIELD);
		dictionary.getRead().execute(context);
		List<CatalogEntry> originalEntries = context.getResults();
        Instrospection instrospection = access.newSession(originalEntries.get(0));

		if (trashableField != null && trashableField.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE
				&& context.getNamespaceContext().isRecycleBinEnabled()) {
			log.trace("Trashing results");

			for (CatalogEntry originalEntry : originalEntries) {
                access.setPropertyValue(trashableField, originalEntry, true, instrospection);
                context.getRequest().setEntry(originalEntry.getId());
				context.getRequest().setEntryValue(originalEntry);
                dictionary.getWrite().execute(context);
			}

		} else {
			log.trace("Deleting results");
			DataDeleteCommand dao = (DataDeleteCommand) deleters.getCommand(String.valueOf(catalog.getStorage()));

			context.setOldValues(originalEntries);
			log.debug("<CatalogActionFilter>");
			preprocess(context,DELETE_ACTION);
			log.debug("</CatalogActionFilter>");

			// single or multiple delete

			if (synthesizer.evaluateGreatAncestor(context,catalog,null)!= null && !catalog.getConsolidated()) {
				dictionary.getRead().execute(context);
				Object parentEntityId = synthesizer.getAllegedParentId((CatalogEntry) context.getEntryResult(), instrospection,access);
				// we are certain this catalog has a parent, otherwise this DAO
				// would
				// not be called
				Long parentCatalogId = catalog.getParent();
				// if parent not found, asume it has been deleted previously
				if (parentEntityId != null) {
					// delegate deeper getInheritance to another instance of an
					// AncestorAware DAO


					context.triggerDelete(parentCatalogId.toString(),null,parentEntityId);

				}

			}

			//FIXME cache invalidation (as with remote cache invalidation) should be handled with an event
			dao.execute(context);
			CatalogResultCache cache = context.getCache(context.getCatalogDescriptor(), context);
			for (CatalogEntry originalEntry : originalEntries) {
				context.getRuntimeContext().getTransactionHistory().didDelete(context, originalEntry, dao);
				if (cache != null) {
					cache.delete(context, catalog.getDistinguishedName(), originalEntry);
				}

			}
            // performAfterDelete
			log.debug("<CatalogActionEvent-Broadcast>");
			postProcess(context,catalog.getDistinguishedName(),DELETE_ACTION,null);
			log.debug("</CatalogActionEvent-Broadcast>");

		}


		return CONTINUE_PROCESSING;
	}

}
