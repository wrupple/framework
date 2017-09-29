package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.event.domain.Instrospection;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static com.wrupple.muba.catalogs.domain.CatalogEvent.CREATE_ACTION;
import static com.wrupple.muba.catalogs.domain.CatalogEvent.DELETE_ACTION;

@Singleton
public class CatalogDeleteTransactionImpl extends CatalogTransaction implements CatalogDeleteTransaction {

	protected static final Logger log = LoggerFactory.getLogger(CatalogDeleteTransactionImpl.class);

	private final CatalogReadTransaction read;

	private final CatalogUpdateTransaction update;


	private final Deleters deleters;

	@Inject
	public CatalogDeleteTransactionImpl(Deleters deleters, CatalogUpdateTransaction update,CatalogReadTransaction read, CatalogFactory factory) {
		this.read = read;
		this.update = update;
		this.deleters = deleters;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		log.trace("[DELETE START]");
		CatalogActionContext context = (CatalogActionContext) c;
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		FieldDescriptor trashableField = catalog.getFieldDescriptor(Trash.TRASH_FIELD);
		read.execute(context);
		List<CatalogEntry> originalEntries = context.getResults();
        Instrospection instrospection = context.getCatalogManager().access().newSession(originalEntries.get(0));

		if (trashableField != null && trashableField.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE
				&& context.getNamespaceContext().isRecycleBinEnabled()) {
			log.trace("Trashing results");

			for (CatalogEntry originalEntry : originalEntries) {
                context.getCatalogManager().access().setPropertyValue(trashableField, originalEntry, true, instrospection);
                context.setEntry(originalEntry.getId());
				context.setEntryValue(originalEntry);
				update.execute(context);
			}

		} else {
			log.trace("Deleting results");
			DataDeleteCommand dao = (DataDeleteCommand) deleters.getCommand(String.valueOf(catalog.getStorage()));

			context.setOldValues(originalEntries);
			log.debug("<CatalogActionFilter>");
			preprocess(context,DELETE_ACTION);
			log.debug("</CatalogActionFilter>");

			// single or multiple delete

			if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
				context.getCatalogManager().getRead().execute(context);
				Object parentEntityId = context.getCatalogManager().getAllegedParentId((CatalogEntry) context.getEntryResult(), instrospection);
				// we are certain this catalog has a parent, otherwise this DAO
				// would
				// not be called
				Long parentCatalogId = catalog.getParent();
				CatalogActionContext childContext = context.getCatalogManager().spawn(context);
				// if parent not found, asume it has been deleted previously
				if (parentEntityId != null) {
					// delegate deeper inheritance to another instance of an
					// AncestorAware DAO
					childContext.setCatalogDescriptor(
							childContext.getCatalogManager().getDescriptorForKey(parentCatalogId, childContext));
					childContext.setEntry(parentEntityId);

					context.getCatalogManager().getDelete().execute(childContext);

				}

			}

			//FIXME cache invalidation (as with remote cache invalidation) should be handled with an event
			dao.execute(context);
			CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);
			for (CatalogEntry originalEntry : originalEntries) {
				context.getTransactionHistory().didDelete(context, originalEntry, dao);
				if (cache != null) {
					cache.delete(context, catalog.getDistinguishedName(), originalEntry);
				}

			}
            // performAfterDelete
			log.debug("<CatalogActionEvent-Broadcast>");
			postProcess(context,catalog.getDistinguishedName(),DELETE_ACTION,null);
			log.debug("</CatalogActionEvent-Broadcast>");

		}

		log.trace("[END] deleted: {}", originalEntries);
		return CONTINUE_PROCESSING;
	}

}
