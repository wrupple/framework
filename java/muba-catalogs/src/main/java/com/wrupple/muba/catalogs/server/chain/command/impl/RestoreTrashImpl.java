package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.RestoreTrash;
import com.wrupple.muba.catalogs.server.domain.FilterDataOrderingImpl;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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

            Instrospection instrospection = context.getCatalogManager().access().newSession(null);
            FilterData all = FilterDataUtils.newFilterData();
			all.setConstrained(false);
			all.addOrdering(new FilterDataOrderingImpl(HasCatalogId.CATALOG_FIELD, false));

			// READ ALL TRASH ITEMS ORDERED BY NUMERIC_ID TYPE
			List<Trash> trash = context.read(Trash.CATALOG,all);
			String catalogId = null;
			CatalogDescriptor descriptor = null;
			FieldDescriptor trashField = null;

			context.setResults(trash);
			context.getRequest().setFilter(null);
			for (Trash entry : trash) {
				if (catalogId == null || !catalogId.equals(entry.getCatalog())) {
					// ONLY CHANGE SERVICES WHEN TRASH TYPE CHANGES, HOPEFULLY
					// OPTIMIZING THE PROCESS SINCE TRASH ITEMS ARE READ ORDERED
					// BY TYPE
					catalogId = entry.getCatalog();
					descriptor = context.getDescriptorForName(catalogId);
					trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
				}
				undelete(e, context, descriptor, trashField, instrospection);
			}
			// DUMP TRASH
			context.delete(Trash.CATALOG,all,null/*requested entry*/);
		} else {

			log.trace("[RESTORE TRASH ITEM] {}", e);

			String catalogId = e.getCatalog();
			CatalogDescriptor descriptor = context.getDescriptorForName(catalogId);
			FieldDescriptor trashField = descriptor.getFieldDescriptor(Trash.TRASH_FIELD);
            Instrospection instrospection = context.getCatalogManager().access().newSession(null);
            context.getRequest().setFilter(null);
			undelete(e, context, descriptor, trashField, instrospection);

			// DUMP TRASH
            context.delete(Trash.CATALOG,null/*filterdata*/,e.getId());

			// SINCE THIS TRIGGER IS PERFORMED BEFORE ACTION IS COMMITED,
			// AND FAILS SILENTLY, then when the restoring action is
			// attempted, it will fail and everything will be fine
			context.addResult(e);
		}

		return CONTINUE_PROCESSING;
	}

	protected void undelete(Trash e, CatalogActionContext context, CatalogDescriptor descriptor,
			FieldDescriptor trashField, Instrospection instrospection) throws Exception {
		if (e.isRestored()) {
			log.trace("[UNDELETE] {}", e);
			Object entryId = e.getEntry();
			String catalogId = e.getCatalog();

			CatalogEntry trashedEntry = context.get(catalogId,entryId);
            context.getCatalogManager().access().setPropertyValue(trashField, trashedEntry, false, instrospection);
			context.write(catalogId,entryId,trashedEntry);

		}
	}
}
