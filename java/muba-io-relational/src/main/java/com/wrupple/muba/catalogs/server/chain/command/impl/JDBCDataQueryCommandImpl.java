package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.bootstrap.domain.FilterDataOrdering;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl.MultipleFieldResultsHandler;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.QueryResultHandler;

@Singleton
public class JDBCDataQueryCommandImpl implements JDBCDataQueryCommand {

	protected static final Logger log = LoggerFactory.getLogger(JDBCDataQueryCommandImpl.class);

	static class FilterDataPayload {
		final StringBuilder filterStringBuffer;
		final Object[] parameterValues;

		public FilterDataPayload(StringBuilder filterStringBuffer, Object[] parameterValues) {
			super();
			if (filterStringBuffer == null) {
				filterStringBuffer = new StringBuilder();
			}
			this.filterStringBuffer = filterStringBuffer;
			this.parameterValues = parameterValues;
		}

		public String getFilter() {
			if (filterStringBuffer == null) {
				return "";
			}
			String filter = filterStringBuffer.toString();
			if (filter != null) {
				if (filter.trim().isEmpty()) {
					return "";
				}
			}
			return filter;
		}

		public Object[] getParameterValues() {
			return parameterValues;
		}

		public StringBuilder getFilterStringBuffer() {
			return filterStringBuffer;
		}

		public void resetStringBuffer() {
			filterStringBuffer.setLength(0);
		}

	}

	private final JDBCMappingDelegate tableNames;
	private final CatalogEvaluationDelegate accessor;
	private final QueryRunner runner;
	private final Provider<QueryResultHandler> rshp;
	private final Boolean multitenant;
	private final DateFormat dateFormat;
	private final int missingTableErrorCode;
	private final String domainField;
	private final char DELIMITER;

	@Inject
	public JDBCDataQueryCommandImpl(QueryRunner runner, Provider<QueryResultHandler> rshp,
			JDBCMappingDelegate tableNames, CatalogEvaluationDelegate accessor,
			@Named("system.multitenant") Boolean multitenant, DateFormat dateFormat,
			@Named("catalog.missingTableErrorCode") Integer missingTableErrorCode,
			@Named("catalog.domainField") String domainField, @Named("catalog.sql.delimiter") Character delimiter) {
		DELIMITER = delimiter;
		this.dateFormat = dateFormat;
		this.rshp = rshp;
		this.runner = runner;
		this.accessor = accessor;
		this.tableNames = tableNames;
		this.domainField = domainField;
		this.multitenant = multitenant;
		this.missingTableErrorCode = missingTableErrorCode;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
		FilterData filter = context.getFilter();

		String query;
		Object[] values;
		FilterDataPayload payload = getFilters(context, catalogDescriptor, filter.getFilters());

		List<? extends FilterDataOrdering> ordering = filter.getOrdering();
		boolean first = true;
		for (FilterDataOrdering ord : ordering) {
			if (first) {
				first = false;
			} else {
				payload.getFilterStringBuffer().append(",");
			}
			payload.getFilterStringBuffer().append("ORDER BY ");
			payload.getFilterStringBuffer().append(DELIMITER);
			payload.getFilterStringBuffer().append(ord.getField());
			payload.getFilterStringBuffer().append(DELIMITER);
			if (ord.isAscending()) {
				payload.getFilterStringBuffer().append(" ASC ");
			} else {
				payload.getFilterStringBuffer().append(" DESC ");
			}
		}
		if (filter.isConstrained()) {
			int offset = filter.getStart();
			int resultCount = filter.getLength();
			payload.getFilterStringBuffer().append(" LIMIT ");
			payload.getFilterStringBuffer().append(offset);
			payload.getFilterStringBuffer().append(",");
			payload.getFilterStringBuffer().append(resultCount);
			payload.getFilterStringBuffer().append(" ");
		}
		query = payload.getFilter();
		values = payload.getParameterValues();

		// GET ENTRIES
		List<CatalogEntry> results = null;
		QueryResultHandler listrsh = rshp.get();
		listrsh.setContext(context);
		try {
			log.debug("[DB query] {} params: {}", query, values);
			if (values == null) {
				results = runner.query(query, listrsh);
			} else {
				results = runner.query(query, listrsh, values);
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == missingTableErrorCode) {
				log.warn("[DB table does not exist] will return empty result set");
				results = Collections.EMPTY_LIST;
			} else {
				log.error("[DB query error]", e);
				throw e;
			}
		}

		log.trace("[DB query] returned {} results", results.size());

		if (!this.multitenant) {
			for (CatalogEntry o : results) {
				o.setDomain(CatalogEntry.WRUPPLE_ID);
			}
		}

		Collection<FieldDescriptor> fields = catalogDescriptor.getFieldsValues();
		String foreignTableName;
		List<Object> fieldValues;
		String queryL;
		Session session = null;
		MultipleFieldResultsHandler handler = null;
		for (FieldDescriptor field : fields) {
			if (field.isMultiple() && !field.isEphemeral()) {
				foreignTableName = tableNames.getTableNameForCatalogField(context, catalogDescriptor, field);
				if (foreignTableName != null) {

					payload.resetStringBuffer();
					tableNames.buildCatalogFieldQuery(payload.getFilterStringBuffer(), foreignTableName, context,
							catalogDescriptor, field);
					if (handler == null) {
						handler = new MultipleFieldResultsHandler(dateFormat, tableNames);
					}
					handler.setField(field);
					queryL = payload.getFilterStringBuffer().toString();
					log.trace("[DB secondary query] {} ", queryL);

					for (CatalogEntry o : results) {
						if (session == null) {
							session = accessor.newSession(o);
						}
						log.trace("[DB secondary query for] {} ", o.getId());
						// FIXME this is horrible, at lest use prepared
						// statements??
						fieldValues = runner.query(queryL, handler, o.getId());
						accessor.setPropertyValue(catalogDescriptor, field, o, fieldValues, session);
					}
				}

			}
		}

		context.setResults(results);

		return CONTINUE_PROCESSING;
	}

	private FilterDataPayload getFilters(CatalogActionContext context, CatalogDescriptor catalogDescriptor,
			List<? extends FilterCriteria> criterias) throws InstantiationException, IllegalAccessException {
		List<Object> values;
		Long domain = context.getDomain();
		StringBuilder filterStringBuffer = null;
		if (criterias == null || criterias.isEmpty()) {
			filterStringBuffer = new StringBuilder(200);
			buildQueryHeader(filterStringBuffer, 0, catalogDescriptor, context, domain);
			values = null;
		} else {
			int criteriaSize = criterias.size();
			int buffersize = 50 + criteriaSize + 1 * 15;
			filterStringBuffer = new StringBuilder(buffersize);
			int writenCriteria = buildQueryHeader(filterStringBuffer, criteriaSize, catalogDescriptor, context, domain);
			values = new ArrayList<Object>(criteriaSize);
			FilterCriteria criteria;
			String rootField;
			FieldDescriptor fieldDescriptor;
			String catalogId = catalogDescriptor.getCatalog();
			for (int i = 0; i < criteriaSize; i++) {
				criteria = criterias.get(i);
				if (criteria != null) {
					rootField = criteria.getPath(0);
					fieldDescriptor = catalogDescriptor.getFieldDescriptor(rootField);
					if (fieldDescriptor == null || !fieldDescriptor.isFilterable()) {
						// ignore
					} else {
						// field is not null, and filterable, and owned by this
						// catalog in the inheritance hierarchy
						if (fieldDescriptor != null && (fieldDescriptor.getOwnerCatalogId() == null
								|| catalogId.equals(fieldDescriptor.getOwnerCatalogId()))) {
							if (writenCriteria > 0) {
								filterStringBuffer.append(" AND ");
							}

							filterStringBuffer.append("(");
							writeFilterDeclaration(filterStringBuffer, criteria, values, i, fieldDescriptor,
									catalogDescriptor, context);
							filterStringBuffer.append(")");
							writenCriteria++;
						} else {
						}
					}

				}
			}
			if (filterStringBuffer.length() < 5) {
				filterStringBuffer.setLength(0);
			}
		}

		return new FilterDataPayload(filterStringBuffer, values == null ? null : values.toArray());
	}

	private int buildQueryHeader(StringBuilder filterStringBuffer, int criteriaSize,
			CatalogDescriptor catalogDescriptor, CatalogActionContext context, Long domain) {
		filterStringBuffer.append("SELECT * FROM ");
		filterStringBuffer.append(DELIMITER);
		tableNames.getTableNameForCatalog(catalogDescriptor, context, filterStringBuffer);
		filterStringBuffer.append(DELIMITER);

		if (criteriaSize > 0 || (this.multitenant && this.domainField != null)) {
			filterStringBuffer.append(" WHERE ");
			if (domain != null) {
				filterStringBuffer.append(DELIMITER);
				filterStringBuffer.append(this.domainField);
				filterStringBuffer.append(DELIMITER);
				filterStringBuffer.append("=");
				filterStringBuffer.append(domain);
				return 1;
			}
		}
		// return the ammount of written criterias
		return 0;
	}

	private void writeFilterDeclaration(StringBuilder buffer, FilterCriteria criteria, List<Object> v,
			int criteriaIndex, FieldDescriptor fieldDescriptor, CatalogDescriptor catalogDescriptor,
			CatalogActionContext context) {
		List<Object> criteriaValues = criteria.getValues();
		int valuesSize = criteriaValues.size();
		String operator = criteria.getOperator();
		boolean equals = FilterData.EQUALS.equals(operator);
		if (equals) {
			// TODO configurable operators?
			operator = "=";
		}
		Object value;
		for (int i = 0; i < valuesSize; i++) {
			value = criteriaValues.get(i);
			buffer.append(DELIMITER);
			buffer.append(tableNames.getColumnForField(context, catalogDescriptor, fieldDescriptor));
			buffer.append(DELIMITER);
			buffer.append(operator);
			buffer.append("?");
			v.add(value);

			if (i < valuesSize - 1) {
				if (equals || FilterData.DIFFERENT.equals(operator)) {
					buffer.append(" || ");
				} else {
					buffer.append(" && ");
				}
			}

		}

	}

}
