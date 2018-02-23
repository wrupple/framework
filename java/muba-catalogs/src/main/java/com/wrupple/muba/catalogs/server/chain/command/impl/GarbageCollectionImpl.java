package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterCriteria;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasEntryId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.event.domain.impl.FilterCriteriaImpl;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;

@Singleton
public class GarbageCollectionImpl implements GarbageCollection {
	private static final Logger log = LoggerFactory.getLogger(GarbageCollectionImpl.class);


	@Inject
	public GarbageCollectionImpl() {
		super();
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		if (context.getNamespaceContext().isGarbageCollectionEnabled()) {

			List<CatalogEntry> results = context.getResults();

			if (results == null) {
				// ??what now???
				log.trace("invoked with an empty result set");
			} else {

				CatalogDescriptor catalog = context.getCatalogDescriptor();

				//TODO descriptor extends identification, if we want the short version we read identifictation
				List<CatalogEntry> names = context.getAvailableCatalogs();

				log.trace("[SEARCHING {} CATALOGS FOR hard keys referencing {}]", names.size(),
						catalog.getDistinguishedName());

				String parentCatalogId = catalog.getDistinguishedName();
				CatalogDescriptor temp;
				Collection<FieldDescriptor> tempFields;
				FilterData garbageFilter;
				FilterCriteria garbageCriteria;
				List<CatalogEntry> collectedGarbage;

				List<Object> ids = new ArrayList<Object>(results.size());
				for (CatalogEntry e : results) {
					ids.add(e.getId());
				}

				for (CatalogEntry idem : names) {

					garbageFilter = null;
					temp = context.getDescriptorForName((String) idem.getId());
					tempFields = temp.getFieldsValues();

					log.trace("[PROCESSING {}]", idem.getId());
					// FIND SIMPLE KEYS
					for (FieldDescriptor linkingField : tempFields) {
						if (linkingField.isKey() && linkingField.isHardKey() && !linkingField.isMultiple()
								&& parentCatalogId.equals(linkingField.getCatalog())) {

							log.trace("field {}@{} is hard not-multiple key referncing {}  ", linkingField.getFieldId(),
									temp.getDistinguishedName(), parentCatalogId);

							if (garbageFilter == null) {
								garbageFilter = FilterDataUtils.newFilterData();
								garbageFilter.setConstrained(false);
							}

							// APPEND-CREATE
							garbageCriteria = new FilterCriteriaImpl(linkingField.getFieldId(), FilterData.EQUALS, ids);
							garbageFilter.addFilter(garbageCriteria);

							// we found a catalog, with a field that is hard
							// linked to the entry just deleted
							// we must delete all entries that match the deleted
							// entry trhough this field

						}
					}

					if (temp.getFieldDescriptor(HasCatalogId.CATALOG_FIELD) != null
							&& temp.getFieldDescriptor(HasEntryId.ENTRY_ID_FIELD) != null) {
						// THIS NUMERIC_ID HAS A COMPOUND KEY GOING ON
						if (garbageFilter == null) {
							garbageFilter = FilterDataUtils.newFilterData();
							garbageFilter.setConstrained(false);
						}
						garbageCriteria = new FilterCriteriaImpl(HasCatalogId.CATALOG_FIELD, FilterData.EQUALS,
								parentCatalogId);
						garbageFilter.addFilter(garbageCriteria);
						garbageCriteria = new FilterCriteriaImpl(HasEntryId.ENTRY_ID_FIELD, FilterData.EQUALS, ids);
						garbageFilter.addFilter(garbageCriteria);

						log.trace(
								"Will search for dinamic Catalog key references of catalogEntryId and catalogId fields");
					}

					if (garbageFilter != null) {
						log.trace("Querying for hard references");

						context.triggerDelete(temp.getDistinguishedName(),garbageFilter,null);

					}
				}

			}

		}

		return CONTINUE_PROCESSING;
	}

}
