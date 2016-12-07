package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.NotSupportedException;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.server.service.ObjectMapper;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

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
	public Context materializeBlankContext(ExcecutionContext parent) {
		return cms.spawn(parent);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		ExcecutionContext requestContext = (ExcecutionContext) ctx;
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
						context.setAction(CatalogActionRequest.READ_ACTION);
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
				context.setAction(CatalogActionRequest.READ_ACTION);
				pressumedCatalogId = null;
			} else {
				context.setAction(request.getAction());
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
					context.getCatalogManager().synthesize(catalogDescriptor);
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
		}

		return CONTINUE_PROCESSING;
	}

}
