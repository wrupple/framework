package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;

@Singleton
public class EntryDeleteTriggerImpl implements EntryDeleteTrigger {
	private final Provider<Trash> trashp;
	private final CatalogCreateTransaction create;

	@Inject
	public EntryDeleteTriggerImpl(Provider<Trash> trashp, CatalogCreateTransaction create) {
		super();
		this.trashp = trashp;
		this.create = create;
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
		Session session = null;
		Boolean trashed;

		CatalogActionContext trashContext = null;
		for (CatalogEntry e : oldValues) {
			if (trashContext == null) {
				session = context.getCatalogManager().newSession(e);
				trashContext = context.getCatalogManager().spawn(context);

				trashContext.setAction(CatalogActionRequest.CREATE_ACTION);
			}
			trashed = (Boolean) context.getCatalogManager().getPropertyValue(catalog, field, e, null, session);
			if (trashed != null && trashed) {
				Trash trashItem = trashp.get();
				trashItem.setName(e.getName());
				trashItem.setEntry(
						context.getCatalogManager().encodeClientPrimaryKeyFieldValue(e.getId(), field, catalog));
				trashItem.setCatalog(catalog.getDistinguishedName());

				trashContext.setCatalog(catalog.getDistinguishedName());
				trashContext.setEntryValue(trashItem);
				trashContext.setDomain((Long) e.getDomain());
				create.execute(trashContext);
			}
		}

		return CONTINUE_PROCESSING;
	}

}
