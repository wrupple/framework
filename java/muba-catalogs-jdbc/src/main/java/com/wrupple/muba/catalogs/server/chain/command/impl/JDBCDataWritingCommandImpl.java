package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.Versioned;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataReadCommand;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataWritingCommand;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.event.domain.Instrospection;

@Singleton
public class JDBCDataWritingCommandImpl extends AbstractWritingCommand implements JDBCDataWritingCommand{
	protected static final Logger log = LoggerFactory.getLogger(JDBCDataWritingCommandImpl.class);

	private final FieldAccessStrategy access;
	private final JDBCMappingDelegate tableNames;
	private final QueryRunner runner;
	private final JDBCDataReadCommand read;
	private final char DELIMITER;
	private String parentKey;

	@Inject
	public JDBCDataWritingCommandImpl(JDBCDataReadCommand read, Provider<CatalogUpdateTransaction> write, FieldAccessStrategy access, QueryRunner runner, JDBCMappingDelegate tableNames, @Named("catalog.ancestorKeyField") String parentKey, @Named("catalog.sql.delimiter") Character delimiter) {
		super(write);
		this.access = access;
		this.parentKey=parentKey;
		this.DELIMITER=delimiter;
		this.runner = runner;
		this.tableNames = tableNames;
		this.read=read;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogDescriptor descriptor = context.getCatalogDescriptor();
		CatalogEntry originalEntry = access.catalogCopy(descriptor, (CatalogEntry) context.getEntryResult());

		CatalogEntry updatedEntry = (CatalogEntry) context.getRequest().getEntryValue();
		Instrospection instrospection = access.newSession(originalEntry);

		updatedEntry.setDomain((Long) originalEntry.getDomain());
		Object id = context.getRequest().getEntry();
		Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
		List<Object> params = new ArrayList<Object>(fields.size());
		Object fieldValue;
		log.trace("[DB UPDATE] building update query");
		StringBuilder builder = new StringBuilder();
		builder.append("UPDATE ");
		builder.append(DELIMITER);
		tableNames.getTableNameForCatalog(descriptor, context, builder);
		builder.append(DELIMITER);
		builder.append(" SET ");
		String foreignTableName;
		boolean first = true;
		StringBuilder delete;

		for (FieldDescriptor field : fields) {
			if (field.isWriteable() && !field.isEphemeral()) {
				fieldValue = access.getPropertyValue(field, updatedEntry, null, instrospection);
				access.setPropertyValue(field, originalEntry, fieldValue, instrospection);
				if (field.isMultiple() && !field.isEphemeral()) {
					// also update (delete and create) and create multiple
					// fields
					log.debug("[DB UPDATE] deleting {} entries ", field.getFieldId());
					foreignTableName = tableNames.getTableNameForCatalogField(context, descriptor, field);
					delete=new StringBuilder(300);
					delete.append("DELETE FROM ");
					delete.append(DELIMITER);
					delete.append(foreignTableName);
					delete.append(DELIMITER);
					delete.append(" WHERE ");
					delete.append(DELIMITER);
					delete.append(parentKey);
					delete.append(DELIMITER);
					delete.append("=");
					delete.append(id);
					tableNames.createForeignValueList(foreignTableName, (Long) id, (List<Object>) fieldValue, runner, log);
				} else {
					if (!first) {
						builder.append(",");
					} else {
						first = false;
					}
					builder.append(DELIMITER);
					builder.append(tableNames.getColumnForField(context, descriptor, field, false));
					builder.append(DELIMITER);
					builder.append("=?");
					params.add(fieldValue);
				}
			}
		}
		context.setResults(Collections.singletonList(originalEntry));
		if (params.isEmpty()) {
			return CONTINUE_PROCESSING;
		}
		builder.append(" WHERE ");
		builder.append(DELIMITER);
		builder.append(tableNames.getColumnForField(context, descriptor,
				descriptor.getFieldDescriptor(descriptor.getKeyField()), false));
		builder.append(DELIMITER);
		builder.append("=?");
		params.add(id);

		if (descriptor.isVersioned()) {
			Long version = (Long) access.getPropertyValue(descriptor.getFieldDescriptor(Versioned.FIELD),
					context.getOldValue(), null, instrospection);
			builder.append(" && ");
			builder.append(Versioned.FIELD);
			builder.append("=?");
			version++;
			log.trace("[DB UPDATE] updated entry version {}",version);
			params.add(version);
		}
		log.debug("[DB UPDATE] excecute update {} params:{}",builder.toString(),params);
		runner.update( builder.toString(), params.toArray());
		
		read.execute(context);
		
		
		return CONTINUE_PROCESSING;
		
	}

}
