package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.NotSupportedException;
import java.util.Collection;
import java.util.List;

@Singleton
public final class CatalogRequestInterpretImpl implements CatalogRequestInterpret {

	private final SystemCatalogPlugin cms;
	private final ObjectMapper mapper;

	@Inject
	public CatalogRequestInterpretImpl(
			SystemCatalogPlugin cms/* , ObjectMapper mapper */) {
		super();
		this.cms = cms;
		this.mapper = null;
	}

	@Override
	public Context materializeBlankContext(RuntimeContext parent) {
		return cms.spawn(parent);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		RuntimeContext requestContext = (RuntimeContext) ctx;
		CatalogActionRequest request = (CatalogActionRequest) requestContext.getServiceContract();
		CatalogActionContext context = requestContext.getServiceContext();

		if (request == null) {
			List<String> tokens = requestContext.getServiceManifest().getGrammar();
			String key, value;
			for (int i = 0; i < tokens.size() && requestContext.hasNext(); i++) {
				key = tokens.get(i);
				value = requestContext.next();
				if (CatalogDescriptor.DOMAIN_TOKEN.equals(key)) {
					/*
					 * TODO change grammar property name from domain to
					 * "namespace" , which is like domain but string and
					 * contains aall domains plus other namespaces. the problem
					 * is domain is a required property by validation, and
					 * that's good and dont't wanna change it
					 */
					context.setNamespace(value);
				} else if (CatalogActionRequest.ENTRY_ID_FIELD.equals(key)) {
					context.setEntry(cms.decodePrimaryKeyToken(value));
				} else if (CatalogActionRequest.CATALOG_FIELD.equals(key)) {
					if (CatalogActionRequest.READ_ACTION.equals(value)) {
						context.setName(CatalogActionRequest.READ_ACTION);
					} else {
						context.setCatalog(value);
					}
				} else {
					context.put(key, value);
				}

			}
		} else {
			// set namespace
			if (request.getDomain() instanceof String) {
				context.setNamespace((String) request.getDomain());
			} else {
				context.setDomain((Long) request.getDomain());
			}

			/*
			 * READ REQUEST DATA
			 */
			String pressumedCatalogId = (String) request.getCatalog();
			if (CatalogActionRequest.READ_ACTION.equals(pressumedCatalogId)) {
				context.setName(CatalogActionRequest.READ_ACTION);
				pressumedCatalogId = null;
			} else {
				context.setName(request.getName());
			}
			context.setCatalog(pressumedCatalogId);

			/*
			 * decode incomming primary key
			 */
			Object targetEntryId = request.getEntry();
			if (targetEntryId != null) {
				targetEntryId = cms.decodePrimaryKeyToken(targetEntryId);
				context.setEntry(targetEntryId);
			}

			/*
			 * Parse incomming raw Catalog Entry (if any)
			 */
			Object catalogEntry = request.getEntryValue();
			CatalogDescriptor catalogDescriptor;
			if (catalogEntry != null) {
				if (catalogEntry instanceof CatalogEntry) {
					// do nothing
				} else {
					catalogDescriptor = context.getCatalogDescriptor();
                    context.getCatalogManager().access().synthesize(catalogDescriptor);
                    throw new NotSupportedException("implementar deserialización ya estaa hecha en algun lado");
				}
			}

			context.setEntryValue(catalogEntry);
			/*
			 * Process Incomming filters (if any)
			 */

			FilterData filter = request.getFilter();
			if (filter != null) {
				catalogDescriptor = context.getCatalogDescriptor();
				Collection<? extends FilterCriteria> filterCriterias;
				String filterCriteriaField;
				FieldDescriptor field;
				filterCriterias = filter.getFilters();
				for (FilterCriteria fieldCriteria : filterCriterias) {
					filterCriteriaField = fieldCriteria.getPath(0);
					field = catalogDescriptor.getFieldDescriptor(filterCriteriaField);
					if (field.isKey()) {
						fieldCriteria.setValues(cms.decodePrimaryKeyFilters(fieldCriteria.getValues()));
					}
				}
			}
			context.setFilter(filter);
			context.setFollowReferences(request.getFollowReferences());
		}

		return CONTINUE_PROCESSING;
	}

}
