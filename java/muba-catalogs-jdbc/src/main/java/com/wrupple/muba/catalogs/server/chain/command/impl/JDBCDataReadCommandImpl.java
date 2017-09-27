package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.AbstractListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataReadCommand;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.QueryResultHandler;
import com.wrupple.muba.event.domain.Instrospection;

@Singleton
public class JDBCDataReadCommandImpl implements JDBCDataReadCommand {

	protected static final Logger log = LoggerFactory.getLogger(JDBCDataReadCommandImpl.class);

	static class MultipleFieldResultsHandler extends AbstractListHandler<Object> {

		private FieldDescriptor field;
		private DateFormat dateFormat;
		private JDBCMappingDelegate tableNames;

		public MultipleFieldResultsHandler(DateFormat dateFormat, JDBCMappingDelegate tableNames) {
			super();
			this.tableNames = tableNames;
			this.dateFormat = dateFormat;
		}

		@Override
		protected Object handleRow(ResultSet rs) throws SQLException {
			int dataType = field.getDataType();
			Object r = tableNames.handleColumnField(rs, field,dataType, 3, dateFormat);
			log.trace("[DB multiple field value read] {}={}", field.getFieldId(), r);
			return r;
		}

		public void setField(FieldDescriptor field) {
			this.field = field;
		}

	}

	private final JDBCMappingDelegate tableNames;
	private final QueryRunner runner;
	private final Provider<QueryResultHandler> rshp;
	private final Boolean multitenant;
	private final DateFormat dateFormat;
	private final char DELIMITER;

	@Inject
	public JDBCDataReadCommandImpl(QueryRunner runner, Provider<QueryResultHandler> rshp,
			JDBCMappingDelegate tableNames,
			@Named("system.multitenant") Boolean multitenant, DateFormat dateFormat,
			@Named("catalog.sql.delimiter") Character delimiter) {
		DELIMITER = delimiter;
		this.dateFormat = dateFormat;
		this.rshp = rshp;
		this.runner = runner;
		this.tableNames = tableNames;
		this.multitenant = multitenant;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		log.trace("[START]");
		CatalogActionContext context = (CatalogActionContext) ctx;
		StringBuilder builder = new StringBuilder(150);
		CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
		Object id = context.getEntry();
		builder.append("SELECT * FROM ");
		builder.append(DELIMITER);
		tableNames.getTableNameForCatalog(catalogDescriptor, context, builder);
		builder.append(DELIMITER);
		builder.append(" WHERE ");
		builder.append(DELIMITER);
		builder.append(tableNames.getColumnForField(context, catalogDescriptor, catalogDescriptor.getFieldDescriptor(catalogDescriptor.getKeyField()), false));
		builder.append(DELIMITER);
		builder.append("=?");
		log.trace("[DB read] {}  id={}", builder.toString(), id);
		QueryResultHandler rsh = rshp.get();
		rsh.setContext(context);
		List<CatalogEntry> results = runner.query(builder.toString(), rsh, id);

		if (results == null || results.isEmpty()) {
			context.setResults(null);
			return CONTINUE_PROCESSING;
		} else {
			context.setResults(results);
		}
		CatalogEntry r = results.get(0);
		if (!this.multitenant) {
			r.setDomain(CatalogEntry.WRUPPLE_ID);
		}
		Collection<FieldDescriptor> fields = catalogDescriptor.getFieldsValues();
		String foreignTableName;
		List<Object> fieldValues;
		Instrospection instrospection = null;
		MultipleFieldResultsHandler handler = null;
		for (FieldDescriptor field : fields) {
			if (field.isMultiple() && !field.isEphemeral()) {
				foreignTableName = tableNames.getTableNameForCatalogField(context, catalogDescriptor, field);
				if (foreignTableName != null) {
					builder.setLength(0);

					tableNames.buildCatalogFieldQuery(builder, foreignTableName, context, catalogDescriptor, field);

					if (handler == null) {
						handler = new MultipleFieldResultsHandler(dateFormat, tableNames);
					}

					handler.setField(field);
					log.trace("[DB secondary read] {}  id={}", builder.toString(), id);
					fieldValues = runner.query(builder.toString(), handler, id);
					if (instrospection == null) {
						instrospection = context.getCatalogManager().access().newSession(r);
					}
					context.getCatalogManager().access().setPropertyValue(field, r, fieldValues, instrospection);
				}
			}
		}
		log.trace("[END]");
		return CONTINUE_PROCESSING;
	}

}
