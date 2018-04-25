package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCreateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataDeleteCommand;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;

@Singleton
public class JDBCDataDeleteCommandImpl extends AbstractDataDeleteCommand implements JDBCDataDeleteCommand {
	protected static final Logger log = LogManager.getLogger(JDBCDataDeleteCommandImpl.class);

	private final JDBCMappingDelegate tableNames;
	private final QueryRunner runner;
	private final char DELIMITER;
	private final CatalogReadTransaction read;
	private final String parentKey;

	@Inject
	public JDBCDataDeleteCommandImpl(QueryRunner runner, JDBCMappingDelegate tableNames,
			CatalogCreateTransaction create, CatalogReadTransaction read,@Named("catalog.ancestorKeyField") String parentKey,	@Named("catalog.sql.delimiter") Character delimiter) {
		super(create);
		this.parentKey=parentKey;
		this.read = read;
		this.runner = runner;
		this.DELIMITER=delimiter;
		this.tableNames = tableNames;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogDescriptor descriptor = context.getCatalogDescriptor();
		Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
		Object deleteKey = context.getRequest().getEntry();
		read.execute(context);
		CatalogEntry deletable = context.getEntryResult();

		if (deletable == null) {
			throw new IllegalArgumentException("Entry Key not found: " + deleteKey);
		}
		String foreignTableName;
		StringBuilder builder = new StringBuilder(100);
		for (FieldDescriptor field : fields) {
			if (field.isMultiple() && !field.isEphemeral()) {
				foreignTableName = tableNames.getTableNameForCatalogField(context, descriptor, field);
				if (foreignTableName != null) {
					log.debug("[DB DELETE] droping records for field {}", field.getFieldId());
					builder.setLength(0);
					builder.append("DELETE FROM ");
					builder.append(DELIMITER);
					builder.append(foreignTableName);
					builder.append(DELIMITER);
					builder.append(" WHERE ");
					builder.append(DELIMITER);
					builder.append(parentKey);
					builder.append(DELIMITER);
					builder.append("=?");
					runner.update(builder.toString(), deleteKey);
				}

			}
		}
		log.debug("[DB DELETE] droping records for entry {}", deleteKey);

		builder.setLength(0);
		builder.append("DELETE FROM ");
		builder.append(DELIMITER);
		tableNames.getTableNameForCatalog(descriptor, context, builder);
		builder.append(DELIMITER);
		builder.append(" WHERE ");
		builder.append(DELIMITER);
		builder.append(tableNames.getColumnForField(context, descriptor,
				descriptor.getFieldDescriptor(descriptor.getKeyField()), false));
		builder.append(DELIMITER);
		builder.append("=?");

		runner.update(builder.toString(), deleteKey);
		// result alreade set by reading transaction
		// context.setResults(Collections.singletonList(deletable));
		return CONTINUE_PROCESSING;

	}

}
