package com.wrupple.base.server.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.AbstractListHandler;

import com.wrupple.muba.catalogs.server.domain.CatalogExcecutionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.desktop.server.service.CatalogTableNameService;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.FilterData;
import com.wrupple.muba.catalogs.domain.FilterDataOrdering;
import com.wrupple.muba.catalogs.domain.Versioned;

public abstract class AbstractJDBC_DAO<T extends CatalogEntry> implements CatalogDataAccessObject<T> {

	protected static class FilterDataPayload {
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

	}

	static class MultipleFieldResultsHandler<Object> extends AbstractListHandler<Object> {

		private FieldDescriptor field;
		private DateFormat dateFormat;

		// @Named("catalog.datePattern") String pattern
		public MultipleFieldResultsHandler(FieldDescriptor field, DateFormat dateFormat) {
			super();
			this.field = field;
			this.dateFormat = dateFormat;
		}

		@Override
		protected Object handleRow(ResultSet rs) throws SQLException {
			int dataType = field.getDataType();
			if (field.isKey()) {
				dataType = CatalogEntry.INTEGER_DATA_TYPE;
			}
			return (Object) handlerColumnField(rs, dataType, 3, dateFormat);
		}

	}

	protected static Object handlerColumnField(ResultSet rs, int dataType, int columnIndex, DateFormat dateFormat) throws SQLException {
		Object r = null;
		switch (dataType) {
		case CatalogEntry.BOOLEAN_DATA_TYPE:
			r = Boolean.valueOf(rs.getBoolean(columnIndex));
			break;
		case CatalogEntry.INTEGER_DATA_TYPE:
			r = Long.valueOf(rs.getLong(columnIndex));
			break;
		case CatalogEntry.NUMERIC_DATA_TYPE:
			r = Double.valueOf(rs.getDouble(columnIndex));
			break;
		case CatalogEntry.LARGE_STRING_DATA_TYPE:
		case CatalogEntry.STRING_DATA_TYPE:
			r = rs.getString(columnIndex);
			break;
		case CatalogEntry.DATE_DATA_TYPE:
			try {
				r = dateFormat.parse(rs.getString(columnIndex));
			} catch (ParseException e) {
				r = null;
			}
		}
		return r;
	}

	private final QueryRunner runner;
	protected final CatalogTableNameService tableNames;
	protected final DateFormat formatt;
	private final Boolean multitenant;
	private final String domainField;

	protected CatalogDescriptor catalog;
	private String overridenTableName;
	protected Long domain;
	private String overridenSelectQuery;
	private Connection connection;

	public AbstractJDBC_DAO(DataSource ds, CatalogTableNameService tableNames, @Named("catalog.datePattern") String datePatt,
			@Named("system.multitenant") Boolean multitenant, @Named("domainField") String domainField) {
		super();
		this.runner = new QueryRunner(ds);
		this.tableNames = tableNames;
		this.formatt = new SimpleDateFormat(datePatt);
		this.multitenant = multitenant;
		this.domainField = domainField;
	}

	@Override
	public T read(String targetEntryId) throws Exception {
		Object id = parseServerKeyString(targetEntryId);
		Connection conn = assertConnection();
		T r = readLongId(conn, id);

		if (!isOngoingTransaction()) {
			conn.close();
		}
		return r;
	}

	private T readLongId(Connection conn, Object id) throws SQLException {
		ResultSetHandler<T> rsh = getRSHandler();
		T r = runner.query(conn, "SELECT * FROM " + getTableName() + " WHERE id=?", rsh, id);
		if (r == null) {
			return null;
		}
		if (this.multitenant) {
			if (!(this.domain.equals(r.getDomain()))) {
				throw new SecurityException("Anauthorized access to entry");
			}
		} else {
			r.setDomain(CatalogEntry.WRUPPLE_ID);
		}
		Collection<FieldDescriptor> fields = getCatalogDescriptor().getOwnedFieldsValues();
		String foreignTableName;
		List<Object> fieldValues;
		for (FieldDescriptor field : fields) {
			if (field.isMultiple() && !field.isEphemeral()) {
				foreignTableName = getForeignTableName(field);
				if (foreignTableName != null) {
					ResultSetHandler<List<Object>> handler = new MultipleFieldResultsHandler(field, formatt);
					fieldValues = runner.query(conn, "SELECT * FROM " + foreignTableName + " WHERE " + CatalogEntry.ANCESTOR_ID_FIELD + "=?", handler, id);
					setProperty(r, field, fieldValues);
				}
			}
		}

		return r;
	}

	@Override
	public List<T> read(FilterData filter) throws Exception {
		String query;
		Object[] values;
		if (overridenSelectQuery == null) {
			FilterDataPayload payload = getFilters(filter.getFilters());

			List<? extends FilterDataOrdering> ordering = filter.getOrdering();
			boolean first = true;
			for (FilterDataOrdering ord : ordering) {
				if (first) {
					first = false;
				} else {
					payload.getFilterStringBuffer().append(",");
				}
				payload.getFilterStringBuffer().append("ORDER BY ");
				payload.getFilterStringBuffer().append(ord.getField());
				if (ord.isAscending()) {
					payload.getFilterStringBuffer().append(" ASC ");
				} else {
					payload.getFilterStringBuffer().append(" DESC ");
				}
			}
			if (filter.isConstrained()) {
				int offset = filter.getStart();
				int resultCount = filter.getEnd() - filter.getStart();
				payload.getFilterStringBuffer().append(" LIMIT ");
				payload.getFilterStringBuffer().append(offset);
				payload.getFilterStringBuffer().append(",");
				payload.getFilterStringBuffer().append(resultCount);
				payload.getFilterStringBuffer().append(" ");
			}
			query = payload.getFilter();
			values = payload.getParameterValues();
		} else {
			query = this.overridenSelectQuery;
			values = null;
		}

		// GET ENTRIES
		List<T> results = null;
		ResultSetHandler<List<T>> listrsh = getRSListHandler();
		Connection conn = assertConnection();

		if (this.overridenSelectQuery == null) {
			try {
				if (values == null) {
					results = runner.query(conn, "SELECT * FROM " + getTableName() + query, listrsh);
				} else {
					results = runner.query(conn, "SELECT * FROM " + getTableName() + query, listrsh, values);
				}
			} catch (SQLException e) {
				if (e.getErrorCode() == 1146) {
					createRequiredTables(conn);
					if (values == null) {
						results = runner.query(conn, "SELECT * FROM " + getTableName() + query, listrsh);
					} else {
						results = runner.query(conn, "SELECT * FROM " + getTableName() + query, listrsh, values);
					}
				} else {
					throw e;
				}
			}
		} else {
			// TODO PASS PARAMETERS TO OVERRIDEN QUERY (at least limits)
			results = runner.query(query, listrsh);
		}

		if (!this.multitenant) {
			for (T o : results) {
				o.setDomain(CatalogEntry.WRUPPLE_ID);
			}
		}

		Collection<FieldDescriptor> fields = getCatalogDescriptor().getOwnedFieldsValues();
		String foreignTableName;
		List<Object> fieldValues;
		String queryL;
		for (FieldDescriptor field : fields) {
			if (field.isMultiple() && !field.isEphemeral()) {
				foreignTableName = getForeignTableName(field);
				if (foreignTableName != null) {
					// FIXME this is horrible, at least use prepared statements
					queryL = "SELECT * FROM " + foreignTableName + " WHERE " + CatalogEntry.ANCESTOR_ID_FIELD + "=?";
					for (T o : results) {
						ResultSetHandler<List<Object>> handler = new MultipleFieldResultsHandler(field, formatt);
						fieldValues = runner.query(conn, queryL, handler, o.getId());
						setProperty(o, field, fieldValues);
					}
				}

			}
		}

		if (!isOngoingTransaction()) {
			conn.close();
		}

		return results;
	}

	@Override
	public T update(T originalEntry, T updatedEntry) throws Exception {

		Connection conn = assertConnection();

		try {

			updatedEntry.setDomain(originalEntry.getDomain());
			Object id = originalEntry.getId();
			Collection<FieldDescriptor> fields = getCatalogDescriptor().getOwnedFieldsValues();
			List<Object> params = new ArrayList<Object>(fields.size());
			Object fieldValue;
			StringBuilder builder = new StringBuilder();
			builder.append("UPDATE ");// " SET height=? WHERE name=?"
			builder.append(getTableName());
			builder.append(" SET ");
			String foreignTableName;
			boolean first = true;

			for (FieldDescriptor field : fields) {
				if (field.isWriteable() && !field.isEphemeral()) {
					fieldValue = getFieldValue(updatedEntry, field);
					setProperty(originalEntry, field, fieldValue);
					if (field.isMultiple()) {
						// also update (delete and create) and create multiple
						// fields
						foreignTableName = tableNames.getTableNameForCatalogFiled(getCatalogDescriptor(), field, domain);
						runner.update(conn, "DELETE FROM " + foreignTableName + " WHERE " + CatalogEntry.ANCESTOR_ID_FIELD + "=?", (Long) id);
						createForeignValueList(conn, foreignTableName, (Long) id, (List<Object>) fieldValue);
					} else {
						if (!first) {
							builder.append(",");
						} else {
							first = false;
						}
						builder.append("`");
						builder.append(getColumnForField(field));
						builder.append("`=?");
						params.add(fieldValue);
					}
				}
			}
			if (params.isEmpty()) {
				return originalEntry;
			}
			builder.append(" WHERE ");
			builder.append(getColumnForField(getCatalogDescriptor().getFieldDescriptor(getCatalogDescriptor().getKeyField())));
			builder.append("=?");
			params.add(id);
			if (this.catalog.isVersioned()) {
				Object version = this.getFieldValue(originalEntry, catalog.getFieldDescriptor(Versioned.FIELD));
				builder.append(" && ");
				builder.append(Versioned.FIELD);
				builder.append("=?");
				params.add(version);
			}
			runner.update(conn, builder.toString(), params.toArray());

			if (!isOngoingTransaction()) {
				conn.commit();
			}

		} catch (Exception e) {
			if (!isOngoingTransaction()) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (!isOngoingTransaction()) {
				conn.close();
			}

		}

		// re-read?
		return originalEntry;
	}

	@Override
	public T create(T o) throws Exception {
		Object id = null;

		Collection<FieldDescriptor> fields = getCatalogDescriptor().getOwnedFieldsValues();
		String fieldId;
		String foreignTableName;
		Object fieldValue;
		StringBuilder builder = new StringBuilder();
		List<Object> paramz = new ArrayList<Object>();
		builder.append("INSERT INTO ");
		builder.append(getTableName());
		builder.append("(");
		StringBuilder values = new StringBuilder(fields.size() * 3 + 5);
		values.append("(");
		String column;
		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			column = getColumnForField(field);
			if (column != null && this.domainField != null && fieldId.equals(this.domainField)) {
				if (this.multitenant) {
					// handled below
				} else {
					throw new SecurityException("Catalog definitions contains reserver field " + this.domainField + " on a non-multitenant system");
				}
			}
			if (column != null && !field.isEphemeral()) {
				if (!field.isCreateable()) {
				} else if (field.isMultiple()) {
				} else {
					fieldValue = getFieldValue(o, field);
					if (paramz.size() > 0) {
						values.append(",");
						builder.append(",");
					}
					paramz.add(fieldValue);
					builder.append("`");
					builder.append(column);
					builder.append("`");
					values.append("?");
				}
			}
		}
		if (this.multitenant && this.domainField != null) {
			values.append(",");
			values.append(this.domain);
			builder.append(",");
			builder.append("`");
			builder.append(this.domainField);
			builder.append("`");
		}
		values.append(")");
		builder.append(")");
		builder.append(" VALUES ");
		builder.append(values);

		JDBCSingleLongKeyResultHandler keyHandler = new JDBCSingleLongKeyResultHandler();
		Connection conn = assertConnection();

		try {

			try {
				id = runner.insert(conn, builder.toString(), keyHandler, paramz.toArray());
			} catch (SQLException e) {
				if (e.getErrorCode() == 1146) {
					createRequiredTables(conn);
					id = runner.insert(conn, builder.toString(), keyHandler, paramz.toArray());
				} else {
					throw e;
				}
			}

			T niu = readLongId(conn, id);

			for (FieldDescriptor field : fields) {
				fieldId = field.getFieldId();
				if (field.isWriteable() && field.isMultiple() && !field.isEphemeral()) {
					fieldValue = getFieldValue(o, field);
					foreignTableName = getForeignTableName(field);
					if (foreignTableName != null) {
						createForeignValueList(conn, foreignTableName, (Long) id, (List<Object>) fieldValue);
						setProperty(niu, field, fieldValue);
					}
				}

			}
			if (!this.multitenant) {
				niu.setDomain(CatalogEntry.WRUPPLE_ID);
			}

			if (!isOngoingTransaction()) {
				conn.commit();
				conn.close();
			}
			return niu;
		} catch (Exception e) {
			if (!isOngoingTransaction()) {
				conn.rollback();
				conn.close();
			}
			throw e;
		}

	}

	@Override
	public T delete(T o) throws Exception {
		Collection<FieldDescriptor> fields = getCatalogDescriptor().getOwnedFieldsValues();
		Connection conn = assertConnection();

		try {

			String foreignTableName;
			for (FieldDescriptor field : fields) {
				if (field.isMultiple() && !field.isEphemeral()) {
					foreignTableName = tableNames.getTableNameForCatalogFiled(getCatalogDescriptor(), field, domain);
					runner.update(conn, "DELETE FROM " + foreignTableName + " WHERE " + CatalogEntry.ANCESTOR_ID_FIELD + "=?", o.getId());

				}
			}
			runner.update(conn, "DELETE FROM " + getTableName() + " WHERE id=?", o.getId());
			if (!isOngoingTransaction()) {
				conn.commit();
			}

		} catch (Exception e) {
			if (!isOngoingTransaction()) {
				conn.rollback();
			}
			throw e;
		} finally {
			if (!isOngoingTransaction()) {
				conn.close();
			}

		}

		return o;
	}

	@Override
	public void setDomain(Long domain) {
		this.domain = domain;
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {
	}

	@Override
	public void beginTransaction() throws NotSupportedException, SystemException {
		if (this.connection == null) {
			try {
				this.connection = assertConnection();
			} catch (SQLException e) {
				throw new SystemException(e.getMessage());
			}
		}
	}

	@Override
	public void commitTransaction() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
			SystemException {
		if (this.connection != null) {
			try {
				this.connection.commit();
				this.connection.close();
			} catch (SQLException e) {
				throw new IllegalStateException(e);
			}

		}
	}

	@Override
	public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		if (this.connection != null) {
			try {
				this.connection.rollback();
			} catch (SQLException e) {
				throw new SystemException(e.getMessage());
			} finally {
				try {
					this.connection.close();
				} catch (SQLException e) {
					throw new SystemException(e.getMessage());
				}
				this.connection = null;
			}
		}
	}

	private void createForeignValueList(Connection conn, String foreignTableName, Long id, List<Object> fieldValue) throws SQLException {
		if (fieldValue == null || fieldValue.isEmpty()) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(foreignTableName);
		builder.append(" VALUES ");
		int size = fieldValue.size();
		Object[] params = new Object[size * 3];
		for (int i = 0; i < size; i++) {
			if (fieldValue.get(i) == null) {
				throw new NullPointerException("Foreign Value Lists may not contain null values");
			}
			if (i != 0) {
				builder.append(',');
			}
			builder.append(" (?,?,?) ");
			params[i * 3] = null;
			params[i * 3 + 1] = id;
			params[i * 3 + 2] = fieldValue.get(i);
		}

		runner.update(conn, builder.toString(), params);
	}

	protected Object parseServerKeyString(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			return id;
		}
	}

	protected FilterDataPayload getFilters(List<? extends FilterCriteria> criterias) throws InstantiationException, IllegalAccessException {
		List<Object> values;
		StringBuilder filterStringBuffer = null;
		if (criterias == null || criterias.isEmpty()) {
			values = null;
		} else {
			int criteriaSize = criterias.size();
			int buffersize = criteriaSize + 1 * 15;
			filterStringBuffer = new StringBuilder(buffersize);
			filterStringBuffer.append(" WHERE ");
			if (this.multitenant && this.domainField != null) {
				if (this.domain == null) {
					throw new NullPointerException("multitenant DAO's domain not specified");
				}
				filterStringBuffer.append(this.domainField);
				filterStringBuffer.append("=");
				filterStringBuffer.append(this.domain);
				filterStringBuffer.append(" && ");
			}
			values = new ArrayList<Object>(criteriaSize);
			FilterCriteria criteria;
			String rootField;
			FieldDescriptor fieldDescriptor;
			String catalogId = getCatalogDescriptor().getCatalog();

			for (int i = 0; i < criteriaSize; i++) {
				criteria = criterias.get(i);
				if (criteria != null) {
					rootField = criteria.getPath(0);
					fieldDescriptor = getCatalogDescriptor().getFieldDescriptor(rootField);
					if (fieldDescriptor == null || !fieldDescriptor.isFilterable()) {
						// ignore
					} else {
						// field is not null, and filterable, and owned by this
						// catalog in the inheritance hierarchy
						if (fieldDescriptor != null && (fieldDescriptor.getOwnerCatalogId() == null || catalogId.equals(fieldDescriptor.getOwnerCatalogId()))) {
							filterStringBuffer.append("(");
							writeFilterDeclaration(filterStringBuffer, criteria, values, i, fieldDescriptor);
							filterStringBuffer.append(")");
							filterStringBuffer.append(" && ");
						} else {
						}
					}

				}
			}
			try {
				filterStringBuffer.delete(filterStringBuffer.length() - 4, filterStringBuffer.length());
			} catch (Exception e) {

			}
			if (filterStringBuffer.length() < 5) {
				filterStringBuffer.delete(0, filterStringBuffer.length());
			}
		}

		return new FilterDataPayload(filterStringBuffer, values == null ? null : values.toArray());
	}

	protected void writeFilterDeclaration(StringBuilder buffer, FilterCriteria criteria, List<Object> v, int criteriaIndex, FieldDescriptor fieldDescriptor) {
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
			buffer.append("`");
			buffer.append(getColumnForField(fieldDescriptor));
			buffer.append("`");
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

	public void setOverridenTableName(String overridenTableName) {
		this.overridenTableName = overridenTableName;
	}

	private void createRequiredTables(Connection conn) throws SQLException {
		CatalogDescriptor catalog = getCatalogDescriptor();
		Collection<FieldDescriptor> fields = catalog.getOwnedFieldsValues();
		String mainTable = getTableName();

		StringBuilder mainstmt = new StringBuilder();
		mainstmt.append("CREATE TABLE IF NOT EXISTS `");
		mainstmt.append(mainTable);
		mainstmt.append("` (");
		mainstmt.append(CatalogEntry.PUBLIC);
		mainstmt.append(" BOOL DEFAULT FALSE,");
		mainstmt.append(CatalogEntry.DRAFT_FIELD);
		mainstmt.append(" BOOL DEFAULT FALSE,");
		String fieldstmt;
		String dbcDataType;
		String primaryKey = catalog.getKeyField();
		boolean first = true;
		for (FieldDescriptor field : fields) {
			if (!field.isEphemeral() && !(CatalogEntry.PUBLIC.equals(field.getFieldId()) || CatalogEntry.DRAFT_FIELD.equals(field.getFieldId()))) {
				dbcDataType = getDataType(field);
				if (field.isMultiple()) {
					mainTable = tableNames.getTableNameForCatalogFiled(catalog, field, domain);
					fieldstmt = "CREATE TABLE IF NOT EXISTS " + mainTable + " ( id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, "
							+ CatalogEntry.ANCESTOR_ID_FIELD + " INT UNSIGNED NOT NULL , INDEX(" + CatalogEntry.ANCESTOR_ID_FIELD + "), value " + dbcDataType
							+ " )";
					runner.update(conn, fieldstmt);
				} else {
					if (first) {
						first = false;
					} else {
						mainstmt.append(",");
					}
					// TODO maybe use double quotes instead of backticks for SQL
					// portability
					mainstmt.append("`");
					mainstmt.append(field.getFieldId());
					mainstmt.append("` ");
					if (field.isKey()) {
						if (primaryKey.equals(field.getFieldId())) {
							mainstmt.append("INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY");
						} else {
							mainstmt.append(dbcDataType);
							mainstmt.append(", INDEX(");
							mainstmt.append(field.getFieldId());
							mainstmt.append(")");
						}
					} else {
						mainstmt.append(dbcDataType);
					}
				}
			}
		}
		mainstmt.append(")");
		runner.update(conn, mainstmt.toString());

	}

	private String getDataType(FieldDescriptor field) {
		int dataType = field.getDataType();
		String r;
		switch (dataType) {
		case CatalogEntry.NUMERIC_DATA_TYPE:
			r = "DOUBLE";
			break;
		case CatalogEntry.INTEGER_DATA_TYPE:
			if (field.isKey()) {
				r = "INT UNSIGNED";
			} else {
				r = "INT";
			}
			break;
		case CatalogEntry.DATE_DATA_TYPE:
			r = "VARCHAR(32)";
			break;
		case CatalogEntry.STRING_DATA_TYPE:
			if (field.isKey()) {
				r = "VARCHAR(64)";
			} else {
				r = "VARCHAR(255)";
			}
			break;
		case CatalogEntry.BOOLEAN_DATA_TYPE:
			r = "BOOLEAN DEFAULT FALSE";
			break;
		case CatalogEntry.BLOB_DATA_TYPE:
			r = "BLOB";
			break;
		case CatalogEntry.LARGE_STRING_DATA_TYPE:
			r = "TEXT";
			break;
		default:
			r = null;
		}
		return r;
	}

	public CatalogDescriptor getCatalog() {
		return catalog;
	}

	public void setCatalog(CatalogDescriptor catalog) {
		this.catalog = catalog;
	}

	protected abstract String getForeignTableName(FieldDescriptor field);

	public String getTableName() {
		if (this.overridenTableName == null) {
			return tableNames.getTableNameForCatalog(getCatalogDescriptor(), domain);
		}
		return overridenTableName;
	}

	private boolean isOngoingTransaction() {
		return this.connection != null;
	}

	private Connection assertConnection() throws SQLException {
		Connection con = this.connection;
		if (con == null) {
			con = runner.getDataSource().getConnection();
			con.setAutoCommit(false);
		}
		return con;
	}

	protected abstract CatalogDescriptor getCatalogDescriptor();

	protected abstract Object getFieldValue(T e, FieldDescriptor field);

	protected abstract void setProperty(T r, FieldDescriptor field, Object fieldValue);

	/**
	 * @param field
	 * @return null if this field is not to be handled
	 */
	protected abstract String getColumnForField(FieldDescriptor field);

	protected abstract ResultSetHandler<T> getRSHandler();

	protected abstract ResultSetHandler<List<T>> getRSListHandler();

	protected void overrideSelectQuery(String string) {
		this.overridenSelectQuery = string;

	}
}
