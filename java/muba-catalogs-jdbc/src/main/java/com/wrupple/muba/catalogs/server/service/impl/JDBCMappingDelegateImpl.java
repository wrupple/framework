package com.wrupple.muba.catalogs.server.service.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;

/**
 * 
 * @author japi
 *
 */
@Singleton
public class JDBCMappingDelegateImpl implements JDBCMappingDelegate {

	private final String DEFAULT_BOOLEAN_COLUMN_DEFINITION;
	private final String CREATE_TABLE;
	private final String PRIMARY_KEY_COLUMN_DEFINITION;
	private final String FOREIGN_KEY_COLUMN_DEFINITION;
	private final String LARGE_STRING;
	private final String BLOB_TYPE;
	private final char DELIMITER;
	private String parentKey;

	@Inject
	public JDBCMappingDelegateImpl(@Named("catalog.ancestorKeyField") String parentKey,
			@Named("catalog.sql.delimiter") Character delimiter, @Named("catalog.sql.createTable") String cREATE_TABLE,
			@Named("catalog.sql.booleanColumnDef") String dEFAULT_BOOLEAN_COLUMN_DEFINITION,
			@Named("catalog.sql.primaryColumnDef") String pRIMARY_KEY_COLUMN_DEFINITION,
			@Named("catalog.sql.foreignKeyColumnDef") String fOREIGN_KEY_COLUMN_DEFINITION,
			@Named("catalog.sql.longStringType") String lARGE_STRING, @Named("catalog.sql.blobType") String bLOB_TYPE) {
		super();
		this.parentKey = parentKey;
		DELIMITER = delimiter;
		CREATE_TABLE = cREATE_TABLE;
		PRIMARY_KEY_COLUMN_DEFINITION = pRIMARY_KEY_COLUMN_DEFINITION;
		DEFAULT_BOOLEAN_COLUMN_DEFINITION = dEFAULT_BOOLEAN_COLUMN_DEFINITION;
		FOREIGN_KEY_COLUMN_DEFINITION = fOREIGN_KEY_COLUMN_DEFINITION;
		LARGE_STRING = lARGE_STRING;
		BLOB_TYPE = bLOB_TYPE;

	}

	@Override
	public void getTableNameForCatalog(CatalogDescriptor catalog, CatalogActionContext context, StringBuilder builder) {
		if (catalog.getClazz() == null || PersistentCatalogEntity.class.equals(catalog.getClazz())) {
			builder.append(context.getDomain());
			builder.append('_');
			builder.append(catalog.getDistinguishedName());
		} else {
			builder.append(catalog.getClazz().getCanonicalName().replace('.', '_'));
		}
	}

	@Override
	public String getColumnForField(CatalogActionContext context, CatalogDescriptor catalogDescriptor,
			FieldDescriptor field, boolean qualified) {
		if (field.isInherited() && catalogDescriptor.isConsolidated()) {
			return null;
		} else {
			if(qualified){
				StringBuilder builder = new StringBuilder(255);
				getTableNameForCatalog(catalogDescriptor, context, builder);
				builder.append('.');
				builder.append('_');
				builder.append(field.getFieldId());
				return builder.toString();
			}else{
				//return "_"+field.getFieldId();
				return field.getFieldId();
			}
			
		}
	}
	

	@Override
	public String getFieldNameForColumn(String columnName, boolean qualified) {
		if(qualified){
			return columnName.substring(columnName.lastIndexOf('.'), columnName.length()-1);
		}else{
			//return columnName.substring(1);
			return columnName;
		}
		
	}

	@Override
	public void createRequiredTables(CatalogActionContext context, CatalogDescriptor catalog, QueryRunner runner,
			Logger log) throws SQLException {
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();

		StringBuilder mainstmt = new StringBuilder(500);
		StringBuilder indexstmt = null;
		mainstmt.append(CREATE_TABLE);
		mainstmt.append(" ");
		mainstmt.append(DELIMITER);
		getTableNameForCatalog(catalog, context, mainstmt);
		mainstmt.append(DELIMITER);
		mainstmt.append(" (");
		/*mainstmt.append(DELIMITER);
		mainstmt.append(CatalogEntry.PUBLIC);
		mainstmt.append(DELIMITER);
		mainstmt.append(' ');
		mainstmt.append(DEFAULT_BOOLEAN_COLUMN_DEFINITION);
		mainstmt.append(" ,");
		mainstmt.append(DELIMITER);
		mainstmt.append(CatalogEntry.DRAFT_FIELD);
		mainstmt.append(DELIMITER);
		mainstmt.append(" ");
		mainstmt.append(DEFAULT_BOOLEAN_COLUMN_DEFINITION);
		mainstmt.append(',');*/
		String dbcDataType;
		String primaryKey = catalog.getKeyField();
		boolean first = true;
		String mainTable,fieldCOlumn;
		List<String> indexes = new ArrayList<String>();
		for (FieldDescriptor field : fields) {
			if (!field.isEphemeral() && (catalog.isConsolidated()||!field.isInherited()||catalog.getKeyField().equals(field.getFieldId()))) {

				dbcDataType = getDataType(field);
				if (dbcDataType != null) {
					if (field.isMultiple()) {
						if (indexstmt == null) {
							indexstmt = new StringBuilder(200);
						} else {
							indexstmt.setLength(0);
						}

						mainTable = getTableNameForCatalogField(context, catalog, field);
						indexstmt.append(CREATE_TABLE);
						indexstmt.append(' ');
						indexstmt.append(DELIMITER);
						indexstmt.append(mainTable);
						indexstmt.append(DELIMITER);
						indexstmt.append(" ( ");
						indexstmt.append(DELIMITER);
						indexstmt.append("id");
						indexstmt.append(DELIMITER);
						indexstmt.append(' ');
						indexstmt.append(PRIMARY_KEY_COLUMN_DEFINITION);
						indexstmt.append(", ");
						indexstmt.append(DELIMITER);
						indexstmt.append(parentKey);
						indexstmt.append(DELIMITER);
						indexstmt.append(' ');
						indexstmt.append(FOREIGN_KEY_COLUMN_DEFINITION);
						indexstmt.append(" ,");
						indexstmt.append(DELIMITER);
						indexstmt.append("value");
						indexstmt.append(DELIMITER);
						indexstmt.append(dbcDataType);
						indexstmt.append(" )");

						log.debug("[CREATED TABLE] {}", mainTable);
						runner.update(indexstmt.toString());

						indexstmt.setLength(0);

						indexstmt.append("CREATE INDEX ");
						indexstmt.append(DELIMITER);
						indexstmt.append(mainTable);
						indexstmt.append('_');
						indexstmt.append(parentKey);
						indexstmt.append(DELIMITER);
						indexstmt.append(" ON ");
						indexstmt.append(DELIMITER);
						indexstmt.append(mainTable);
						indexstmt.append(DELIMITER);
						indexstmt.append(" ( ");
						indexstmt.append(DELIMITER);
						indexstmt.append(parentKey);
						indexstmt.append(DELIMITER);
						indexstmt.append(" )");
						indexes.add(indexstmt.toString());

					} else {
						if (first) {
							first = false;
						} else {
							mainstmt.append(",");
						}
						fieldCOlumn =getColumnForField(context, catalog, field, false);
						mainstmt.append(DELIMITER);
						mainstmt.append(fieldCOlumn);
						mainstmt.append(DELIMITER);
						mainstmt.append(' ');
						if (field.isKey()) {
							if (primaryKey.equals(field.getFieldId())) {
								mainstmt.append(PRIMARY_KEY_COLUMN_DEFINITION);
							} else {
								mainstmt.append(dbcDataType);

								if (indexstmt == null) {
									indexstmt = new StringBuilder(200);
								} else {
									indexstmt.setLength(0);
								}

								indexstmt.append("CREATE INDEX ");
								indexstmt.append(DELIMITER);
								getTableNameForCatalog(catalog, context, indexstmt);
								indexstmt.append('_');
								indexstmt.append(fieldCOlumn);
								indexstmt.append(DELIMITER);
								indexstmt.append(" ON ");
								indexstmt.append(DELIMITER);
								getTableNameForCatalog(catalog, context, indexstmt);
								indexstmt.append(DELIMITER);
								indexstmt.append(" ( ");
								indexstmt.append(DELIMITER);
								indexstmt.append(fieldCOlumn);
								indexstmt.append(DELIMITER);
								indexstmt.append(" )");
								indexes.add(indexstmt.toString());
							}
						} else {
							mainstmt.append(dbcDataType);
						}
					}
				}

			}
		}
		mainstmt.append(")");
		// create indexes
		log.debug("[CREATED TABLE] {}", catalog.getDistinguishedName());
		runner.update(mainstmt.toString());
		for (String st : indexes) {
			runner.update(st);
		}

	}

	@Override
	public String getTableNameForCatalogField(CatalogActionContext context, CatalogDescriptor catalogDescriptor,
			FieldDescriptor field) {
		StringBuilder builder = new StringBuilder(70);
		getTableNameForCatalog(catalogDescriptor, context, builder);
		builder.append('_');
		builder.append(field.getFieldId());
		return builder.toString();
	}

	@Override
	public void buildCatalogFieldQuery(StringBuilder builder, String foreignTableName, CatalogActionContext context,
			CatalogDescriptor catalogDescriptor, FieldDescriptor field) {
		builder.append("SELECT * FROM ");
		builder.append(DELIMITER);
		builder.append(foreignTableName);
		builder.append(DELIMITER);

		builder.append(" WHERE ");
		builder.append(DELIMITER);
		builder.append(parentKey);
		builder.append(DELIMITER);
		builder.append("=?");
	}

	@Override
	public void createForeignValueList(String foreignTableName, Long id, List<Object> fieldValue, QueryRunner runner,
			Logger log) throws SQLException {
		if (fieldValue == null || fieldValue.isEmpty()) {
			log.trace("[DB secondary insert] No values to insert  ");
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO ");
		builder.append(DELIMITER);
		builder.append(foreignTableName);
		builder.append(DELIMITER);
		builder.append(" VALUES ");
		int size = fieldValue.size();
		Object[] params = new Object[size * 3];
		for (int i = 0; i < size; i++) {
			if (fieldValue.get(i) == null) {
				throw new NullPointerException(
						"Foreign Value Lists may not contain null values. table:" + foreignTableName);
			}
			if (i != 0) {
				builder.append(',');
			}
			builder.append(" (?,?,?) ");
			params[i * 3] = null;
			params[i * 3 + 1] = id;
			params[i * 3 + 2] = fieldValue.get(i);
		}
		log.debug("[DB secondary insert] {} params: {}", builder.toString(), params);
		runner.update(builder.toString(), params);

	}

	@Override
	public Object handleColumnField(ResultSet rs, FieldDescriptor field, int dataType, int columnIndex,
			DateFormat dateFormat) throws SQLException {
		Object r = null;
		if(rs.getObject(columnIndex)!=null){
			switch (dataType) {
				case CatalogEntry.BOOLEAN_DATA_TYPE:
					r = (Object) Boolean.valueOf(rs.getBoolean(columnIndex));
					break;
				case CatalogEntry.INTEGER_DATA_TYPE:
					if (field.getDefaultValueOptions() == null || field.getDefaultValueOptions().isEmpty()) {
						r = (Object) Long.valueOf(rs.getLong(columnIndex));
					} else {
						r = (Object) rs.getInt(columnIndex);
					}

					break;
				case CatalogEntry.NUMERIC_DATA_TYPE:
					r = (Object) Double.valueOf(rs.getDouble(columnIndex));
					break;
				case CatalogEntry.LARGE_STRING_DATA_TYPE:
				case CatalogEntry.STRING_DATA_TYPE:
					r = (Object) rs.getString(columnIndex);
					break;
				case CatalogEntry.OBJECT_DATA_TYPE:
					try {
						r = Class.forName(rs.getString(columnIndex));
					} catch (ClassNotFoundException e) {
						throw new IllegalArgumentException("this catalog jdbc implementation only supports deserialization of clazz type objects");
					}
					break;
				case CatalogEntry.DATE_DATA_TYPE:
					Timestamp date = rs.getTimestamp(columnIndex);
					if(date!=null){
						r = Date.from(date.toInstant());// (Object)
						// dateFormat.parse(rs.getString(columnIndex));
					}

			}
		}

		return r;
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
				r = FOREIGN_KEY_COLUMN_DEFINITION;
			} else {
				r = "INT";
			}
			break;
		case CatalogEntry.DATE_DATA_TYPE:
			r = "TIMESTAMP WITH TIME ZONE";// "VARCHAR(32)";
			break;
		case CatalogEntry.STRING_DATA_TYPE:
			if (field.isKey()) {
				r = "VARCHAR(64)";
			} else {
				r = "VARCHAR(255)";
			}
			break;
		case CatalogEntry.BOOLEAN_DATA_TYPE:
			r = DEFAULT_BOOLEAN_COLUMN_DEFINITION;
			break;
		case CatalogEntry.OBJECT_DATA_TYPE:
			r = LARGE_STRING;
			break;
		case CatalogEntry.BLOB_DATA_TYPE:
			r = BLOB_TYPE;
			break;
		case CatalogEntry.LARGE_STRING_DATA_TYPE:
			r = LARGE_STRING;
			break;
		default:
			throw new UnsupportedOperationException("this JDBC implementation of catalogs does not support storage of field "+field.getFieldId());
		}
		return r;
	}


}
