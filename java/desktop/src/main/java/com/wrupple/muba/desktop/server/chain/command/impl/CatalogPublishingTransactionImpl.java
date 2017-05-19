package com.wrupple.muba.desktop.server.chain.command.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogUpdateTransactionImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.DataStoreManager;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.desktop.server.chain.command.CatalogPublishingTransaction;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;

public class CatalogPublishingTransactionImpl extends CatalogUpdateTransactionImpl implements CatalogPublishingTransaction {
	protected final CatalogPropertyAccesor access;

	@Inject
	public CatalogPublishingTransactionImpl(CatalogPropertyAccesor access, DataStoreManager daoFactory) {
		super(daoFactory);
		this.access = access;
	}

	@Override
	protected CatalogEntry getUpdatedEntry(CatalogExcecutionContext context, CatalogEntry originalEntry) throws Exception {
		CatalogDescriptorImpl catalog = (CatalogDescriptorImpl) context.getCatalogDescriptor();
		Session session = access.newSession(originalEntry);

		publishLinkedResources(originalEntry, session, catalog, context);

		return originalEntry;
	}

	private void publishLinkedResources(CatalogEntry originalEntry, Session session, CatalogDescriptor catalog, CatalogExcecutionContext context)
			throws Exception {
		session.resample(originalEntry);
		FieldDescriptor field = catalog.getFieldDescriptor(CatalogEntry.PUBLIC);
		if (field != null) {
			access.setPropertyValue(catalog, field, originalEntry, true, session);
		}

		Long foreignKey;
		CatalogEntry foreignEntry;
		List<Long> foreignKeys;
		List<CatalogEntry> foreignValues;
		String foreignCatalog;
		CatalogDataAccessObject<CatalogEntry> dao;
		Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
		for (FieldDescriptor f : fields) {
			if (ImplicitJoinUtils.isJoinableValueField(f)) {
				if (f.isKey()) {
					foreignCatalog = f.getForeignCatalogName();
					dao = daoFactory.getOrAssembleDataSource(foreignCatalog, context, CatalogEntry.class);
					if (f.isMultiple()) {
						foreignKeys = (List<Long>) access.getPropertyValue(catalog, f, originalEntry, null, session);
						if (foreignKeys != null && !foreignKeys.isEmpty()) {
							FilterData filterData = FilterDataUtils.createSingleKeyFieldFilter(CatalogEntry.ID_FIELD, foreignKeys);
							foreignValues = dao.read(filterData);
							for (CatalogEntry foreign : foreignValues) {
								publishLinkedResources(foreign, session, dao.getCatalog(), context);
								dao.update(foreign, foreign);
							}
						}
					} else {
						foreignKey = (Long) access.getPropertyValue(catalog, f, originalEntry, null, session);
						if (foreignKey != null) {
							foreignEntry = dao.read(String.valueOf(foreignKey));
							publishLinkedResources(foreignEntry, session, dao.getCatalog(), context);
							dao.update(foreignEntry, foreignEntry);
						}
					}
				}
			}
		}
	}
}
