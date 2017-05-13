package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDeleteTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataCreationCommand;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;
import com.wrupple.muba.catalogs.server.service.impl.JDBCSingleLongKeyResultHandler;

@Singleton
public class JDBCDataCreationCommandImpl extends AbstractDataCreationCommand implements JDBCDataCreationCommand {

	protected static final Logger log = LoggerFactory.getLogger(JDBCDataCreationCommandImpl.class);

	private final JDBCMappingDelegate tableNames;
	private final SQLCompatibilityDelegate compatibility;

	private final QueryRunner runner;

	private final int missingTableErrorCode;

	private final CatalogReadTransaction read;
	private final char DELIMITER;

	private final JDBCSingleLongKeyResultHandler keyHandler;

	@Inject
	public JDBCDataCreationCommandImpl(QueryRunner runner, CatalogDeleteTransaction delete, CatalogReadTransaction read,
			JDBCMappingDelegate tableNames, SQLCompatibilityDelegate compatibility, 
			@Named("catalog.missingTableErrorCode") Integer missingTableErrorCode /*
																					 * 1146
																					 * in
																					 * MySQL
																					 */,
			@Named("catalog.sql.delimiter") Character delimiter) {
		super(delete);
		this.compatibility = compatibility;
		DELIMITER = delimiter;
		keyHandler = new JDBCSingleLongKeyResultHandler();
		this.read = read;
		this.missingTableErrorCode = missingTableErrorCode;
		this.runner = runner;
		this.tableNames = tableNames;
	}
	
	

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogDescriptor catalogDescriptor = context.getCatalogDescriptor();
		CatalogEntry e = (CatalogEntry) context.getEntryValue();
		e.setDomain((Long) context.getDomain());
		log.trace("[Will create Entry] {} in domain {}", e,e.getDomain());
		Session session = context.getCatalogManager().newSession(e);
		Object id = null;

		Collection<FieldDescriptor> fields = catalogDescriptor.getFieldsValues();
		String foreignTableName;
		Object fieldValue;
		StringBuilder builder = new StringBuilder();
		List<Object> paramz = new ArrayList<Object>();
		builder.append("INSERT INTO ");
		builder.append(DELIMITER);
		tableNames.getTableNameForCatalog(catalogDescriptor, context, builder);
		builder.append(DELIMITER);
		builder.append(" (");
		StringBuilder values = new StringBuilder(fields.size() * 3 + 5);
		values.append("(");
		String column;
		for (FieldDescriptor field : fields) {
			column = tableNames.getColumnForField(context, catalogDescriptor, field, false);

			if (column != null && !field.isEphemeral() && (catalogDescriptor.isConsolidated()||!field.isInherited()||catalogDescriptor.getKeyField().equals(field.getFieldId()))) {
				if (!field.isCreateable()) {
				} else if (field.isMultiple()) {
				} else {
					fieldValue = context.getCatalogManager().getPropertyValue(catalogDescriptor, field, e, null, session);
					if (paramz.size() > 0) {
						values.append(",");
						builder.append(",");
					}
					if( CatalogEntry.OBJECT_DATA_TYPE==field.getDataType()){
						//use conversion strategy and object mapper to achieve full serialization? is this even desirable?
						if(fieldValue==null){
							paramz.add(fieldValue);
						}else{
							paramz.add(((Class)fieldValue).getCanonicalName());
						}
						
					}else{
						paramz.add(fieldValue);
					}
					
					builder.append(DELIMITER);
					builder.append(column);
					builder.append(DELIMITER);
					values.append("?");
				}
			}
		}
		/*if (this.multitenant && this.domainField != null) {
			values.append(",");
			values.append(context.getDomain());
			builder.append(",");
			builder.append(DELIMITER);
			builder.append(this.domainField);
			builder.append(DELIMITER);
		}*/
		values.append(")");
		builder.append(")");
		builder.append(" VALUES ");
		builder.append(values);

		compatibility.alterInsertStatement(context, builder);

		try {
			log.debug("[DB insert] {} params: {}", builder.toString(), paramz);
			id = runner.insert(builder.toString(), keyHandler, paramz.toArray());
		} catch (SQLException ee) {
			if (ee.getErrorCode() == missingTableErrorCode) {
				log.warn("[DB table does not exist] will create catalog's table structure");
				tableNames.createRequiredTables(context, catalogDescriptor, runner, log);
				id = runner.insert(builder.toString(), keyHandler, paramz.toArray());
			} else {
				log.error("[DB error while inserting] errorCode = {}", ee.getErrorCode(), ee);
				throw ee;
			}
		}
		if (id == null) {
			id = compatibility.getLastInsertedId(context, runner, keyHandler);
		}
		if (id == null) {
			/*
			 * returning_clause Oracle INSERT INTO table_a ( name,date) val (
			 * 'Ryan','2014.01.01') RETURNING id INTO ?
			 */
			throw new IllegalStateException("INSERT statement did not return id of created entry");
		}
		log.trace("[DB insert] created entry id {}", id);

		for (FieldDescriptor field : fields) {
			if (field.isWriteable() && field.isMultiple() && !field.isEphemeral()) {
				fieldValue = context.getCatalogManager().getPropertyValue(catalogDescriptor, field, e, null, session);
				if (fieldValue != null) {
					foreignTableName = tableNames.getTableNameForCatalogField(context, catalogDescriptor, field);
					if (foreignTableName != null) {
						tableNames.createForeignValueList(foreignTableName, (Long) id, (List<Object>) fieldValue,
								runner, log);
					}
				}

			}
		}
		log.debug("[CREATE DONE] {}/{} ",catalogDescriptor.getDistinguishedName(),id);
		
		context.setCatalogDescriptor(catalogDescriptor);
		context.setFilter(null);
		context.setEntry(id);
		context.put(CatalogReadTransaction.READ_GRAPH,true);
		read.execute(context);
		return CONTINUE_PROCESSING;
	}

}
