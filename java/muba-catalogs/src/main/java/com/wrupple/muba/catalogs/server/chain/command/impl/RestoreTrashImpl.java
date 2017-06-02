package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.RestoreTrash;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;

@Singleton
public class RestoreTrashImpl implements RestoreTrash {
	private static Logger log = LoggerFactory.getLogger(RestoreTrashImpl.class);

	@Inject
	public RestoreTrashImpl( ) {
		super();
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		Trash e = (Trash) context.getEntryResult();
		if (e == null) {
			log.warn("[RESTORE ALL TRASH ITEMS]");

			Session session = context.getCatalogManager().newSession(null);
			FilterData all = FilterDataUtils.newFilterData();
			all.setConstrained(false);
			all.addOrdering(new FilterDataOrderingImpl(HasCatalogId.CATALOG_FIELD, false));

			// READ ALL TRASH ITEMS ORDERED BY NUMERIC_ID TYPE
			context.setFilter(all);
			context.getCatalogManager().getRead().execute(context);
			List<Trash> trash = context.getResults();
			String catalogId = null;
			CatalogDescriptor descriptor = null;
			FieldDescriptor trashField = null;

			context.setResults(trash);
			context.setFilter(null);
			for (Trash entry : trash) {
				if (catalogId == null || !catalogId.equals(entry.getCatalog())) {
					// ONLY CHANGE SERVICES WHEN TRASH TYPE CHANGES, HOPEFULLY
					// OPTIMIZING THE PROCESS SINCE TRASH ITEMS ARE READ ORDERED
					// BY TYPE
					catalogId = entry.getCatalog();
					descriptor = context.getCatalogManager().getDescriptorForName(catalogId, context);
					trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
				}
				undelete(e, context, descriptor, trashField, session);
			}
			// DUMP TRASH
			context.setCatalog(Trash.CATALOG);
			context.setFilter(all);
			context.setEntry(null);
			context.getCatalogManager().getDelete().execute(context);
		} else {

			log.trace("[RESTORE TRASH ITEM] {}", e);

			String catalogId = e.getCatalog();
			CatalogDescriptor descriptor = context.getCatalogManager().getDescriptorForName(catalogId, context);
			FieldDescriptor trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
			Session session = context.getCatalogManager().newSession(null);
			context.setFilter(null);
			undelete(e, context, descriptor, trashField, session);

			// DUMP TRASH
			context.setCatalog(Trash.CATALOG);
			context.setFilter(null);
			context.setEntry(e.getId());
			context.getCatalogManager().getDelete().execute(context);

			// SINCE THIS TRIGGER IS PERFORMED BEFORE ACTION IS COMMITED,
			// AND FAILS SILENTLY, then when the restoring action is
			// attempted, it will fail and everything will be fine
			context.addResult(e);
		}

		return CONTINUE_PROCESSING;
	}

	protected void undelete(Trash e, CatalogActionContext context, CatalogDescriptor descriptor,
			FieldDescriptor trashField, Session session) throws Exception {
		if (e.isRestored()) {
			log.trace("[UNDELETE] {}", e);
			Object entryId = e.getEntry();
			String catalogId = e.getCatalog();
			context.setCatalog(catalogId);
			context.setEntry(entryId);
			context.getCatalogManager().getRead().execute(context);
			CatalogEntry trashedEntry = context.getEntryResult();
			context.getCatalogManager().setPropertyValue(trashField, trashedEntry, false, session);
			context.setEntryValue(trashedEntry);
			context.getCatalogManager().getWrite().execute(context);

		}
	}
}
