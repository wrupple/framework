package com.wrupple.muba.desktop.server.chain.command.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyEncodingService;
import com.wrupple.muba.catalogs.shared.services.CatalogTokenInterpret;
import com.wrupple.muba.desktop.server.service.JacksonCatalogDeserializationService;
import com.wrupple.vegetate.domain.*;
import com.wrupple.vegetate.server.domain.FilterCriteriaImpl;
import com.wrupple.vegetate.server.domain.FilterDataImpl;
import com.wrupple.vegetate.server.domain.FilterDataOrderingImpl;
import com.wrupple.vegetate.server.services.ObjectMapper;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Collection;
import java.util.List;

public final class CatalogRequestInterpretImpl implements CatalogRequestInterpret {

	private final Provider<CatalogTokenInterpret> interpret;
	private final JacksonCatalogDeserializationService entrySerializationService;
	private final PrimaryKeyEncodingService pkes;
	private final ObjectMapper mapper;

	@Inject
	public CatalogRequestInterpretImpl(PrimaryKeyEncodingService pkes, JacksonCatalogDeserializationService entry, Provider<CatalogTokenInterpret> interpret,
			ObjectMapper mapper) {
		super();
		this.entrySerializationService = entry;
		this.interpret = interpret;
		this.pkes = pkes;
		this.mapper = mapper;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		c.put(WriteAuditTrails.CONTEXT_START, System.currentTimeMillis());
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;

		/*
		 * READ REQUEST DATA
		 */
		String pressumedCatalogId = (String) context.getCatalog();

		String targetEntryId = context.getEntry();

		if (CatalogActionRequest.LIST_ACTION_TOKEN.equals(pressumedCatalogId)) {
			return CONTINUE_PROCESSING;
		}

		/*
		 * CATALOG_TIMELINE THIS REQUEST'S ACTION POINTS TO
		 */

		CatalogDescriptor catalogDescriptor = interpret.get().getDescriptorForName(pressumedCatalogId, context.getDomain());
		context.setCatalogDescriptor(catalogDescriptor);
		assert catalogDescriptor != null;

		/*
		 * decode incomming primary key
		 */
		if (targetEntryId != null) {
			targetEntryId = pkes.decodePrimaryKeyToken(targetEntryId, catalogDescriptor);
			context.setEntry(targetEntryId);
		}

		/*
		 * Parse incomming raw Catalog Entry (if any)
		 */
		Object catalogEntry = context.getCatalogEntry();
		if (catalogEntry != null) {
			if (catalogEntry instanceof ObjectNode) {
				ObjectNode rawData = (ObjectNode) catalogEntry;
				CatalogEntry entry = this.entrySerializationService.deserialize(rawData, catalogDescriptor, context);
				context.setCatalogEntry(entry);

			}
		}
		/*
		 * Process Incomming filters (if any)
		 */

		FilterDataImpl filter = context.getFilter();

		if (filter != null) {

			if (context.getDomainContext().isRecycleBinEnabled() && !filter.containsKey(Trash.TRASH_FIELD)) {
				filter.addFilter(new FilterCriteriaImpl(Trash.TRASH_FIELD, FilterData.EQUALS, false));
			}
			Collection<? extends FilterCriteria> filterCriterias;
			String filterCriteriaField;
			FieldDescriptor field;
			filterCriterias = filter.getFilters();
			for (FilterCriteria fieldCriteria : filterCriterias) {
				filterCriteriaField = fieldCriteria.getPath(0);
				field = catalogDescriptor.getFieldDescriptor(filterCriteriaField);
				if (field.isKey()) {
					fieldCriteria.setValues(pkes.decodePrimaryKeyFilters(fieldCriteria.getValues()));
				}
			}
			List<String> rawAppliedFilters = catalogDescriptor.getAppliedCriteria();
			if (rawAppliedFilters != null) {
				FilterCriteriaImpl appliedFilter;
				for (String rawCriteria : rawAppliedFilters) {
					appliedFilter = mapper.readValue(rawCriteria, FilterCriteriaImpl.class);
					filter.addFilter(appliedFilter);
				}
			}
			List<String> rawAppliedSorts = catalogDescriptor.getAppliedSorts();
			if (rawAppliedSorts != null) {
				FilterDataOrderingImpl appliedSort;
				for (String rawSort : rawAppliedSorts) {
					appliedSort = mapper.readValue(rawSort, FilterDataOrderingImpl.class);
					filter.addOrdering(appliedSort);
				}
			}
		}

		return CONTINUE_PROCESSING;
	}
}
