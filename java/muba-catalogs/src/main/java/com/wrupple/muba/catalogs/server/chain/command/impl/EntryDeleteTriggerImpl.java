package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class EntryDeleteTriggerImpl implements EntryDeleteTrigger {
	private final Provider<Trash> trashp;
	private final CatalogKeyServices keydelegate;
	private final FieldAccessStrategy access;

	@Inject
	public EntryDeleteTriggerImpl(Provider<Trash> trashp, CatalogKeyServices keydelegate, FieldAccessStrategy access) {
		super();
		this.access=access;
		this.trashp = trashp;
		this.keydelegate=keydelegate;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;

		CatalogDescriptor catalog = context.getCatalogDescriptor();
		FieldDescriptor field = catalog.getFieldDescriptor(Trash.TRASH_FIELD);

		List<CatalogEntry> oldValues = context.getOldValues();
		if (oldValues == null) {
			oldValues = context.getResults();
		}
		Instrospection instrospection = null;
		Boolean trashed;



		for (CatalogEntry e : oldValues) {

            trashed = (Boolean) access.getPropertyValue(field, e, null, instrospection);
            if (trashed != null && trashed) {
				Trash trashItem = trashp.get();
				trashItem.setName(e.getName());
				trashItem.setEntry(keydelegate.encodeClientPrimaryKeyFieldValue(e.getId(), field, catalog));
				trashItem.setCatalog(catalog.getDistinguishedName());

				//TODO ?? trashItem.setDomain((Long) e.getDomain());
				context.triggerCreate(Trash.CATALOG,trashItem);
			}
		}

		return CONTINUE_PROCESSING;
	}

}
