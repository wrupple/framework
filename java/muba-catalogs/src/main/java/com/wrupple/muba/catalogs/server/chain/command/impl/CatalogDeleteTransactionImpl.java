package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogChangeEvent;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.domain.CatalogChangeEventImpl;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.Deleters;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CatalogDeleteTransactionImpl implements CatalogDeleteTransaction {

	protected static final Logger log = LoggerFactory.getLogger(CatalogDeleteTransactionImpl.class);

	private final CatalogActionTriggerHandler trigerer;

	private final CatalogReadTransaction read;

	private final CatalogUpdateTransaction update;


	private final Deleters deleters;

	@Inject
	public CatalogDeleteTransactionImpl(Deleters deleters, CatalogUpdateTransaction update,
			CatalogActionTriggerHandler trigerer, CatalogReadTransaction read, CatalogFactory factory) {
		this.trigerer = trigerer;
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
        Session session = context.getCatalogManager().access().newSession(originalEntries.get(0));

		if (trashableField != null && trashableField.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE
				&& context.getNamespaceContext().isRecycleBinEnabled()) {
			log.trace("Trashing results");

			for (CatalogEntry originalEntry : originalEntries) {
                context.getCatalogManager().access().setPropertyValue(trashableField, originalEntry, true, session);
                context.setEntry(originalEntry.getId());
				context.setEntryValue(originalEntry);
				update.execute(context);
			}

		} else {
			log.trace("Deleting results");
			DataDeleteCommand dao = (DataDeleteCommand) deleters.getCommand(String.valueOf(catalog.getStorage()));

			context.setOldValues(originalEntries);
			context.setName(CatalogActionRequest.DELETE_ACTION);
			// performBeforeDelete
			trigerer.execute(context);

			// single or multiple delete

			if (catalog.getGreatAncestor() != null && !catalog.isConsolidated()) {
				context.getCatalogManager().getRead().execute(context);
				Object parentEntityId = context.getCatalogManager().getAllegedParentId((CatalogEntry) context.getEntryResult(), session);
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

			dao.execute(context);
			CatalogResultCache cache = context.getCatalogManager().getCache(context.getCatalogDescriptor(), context);
			for (CatalogEntry originalEntry : originalEntries) {
				context.getTransactionHistory().didDelete(context, originalEntry, dao);
				if (cache != null) {
					cache.delete(context, catalog.getDistinguishedName(), originalEntry);
				}

				CatalogChangeEvent event = new CatalogChangeEventImpl((Long) context.getDomain(),
						catalog.getDistinguishedName(), CatalogActionRequest.DELETE_ACTION, originalEntry);
				// cache invalidation
				context.getRootAncestor().addBroadcastable(event);
			}

			// performAfterDelete
			trigerer.postprocess(context, context.getRuntimeContext().getCaughtException());

		}

		log.trace("[END] deleted: {}", originalEntries);
		return CONTINUE_PROCESSING;
	}

}
