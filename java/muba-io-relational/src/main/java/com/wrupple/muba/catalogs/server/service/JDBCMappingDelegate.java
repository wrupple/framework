package com.wrupple.muba.catalogs.server.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

public interface JDBCMappingDelegate {

	void getTableNameForCatalog(CatalogDescriptor catalog, CatalogActionContext context, StringBuilder builder);

	String getColumnForField(CatalogActionContext context, CatalogDescriptor catalogDescriptor, FieldDescriptor field);

	void createRequiredTables(CatalogActionContext context, CatalogDescriptor catalogDescriptor, QueryRunner runner, Logger log) throws SQLException;

	String getTableNameForCatalogField(CatalogActionContext context, CatalogDescriptor catalogDescriptor,FieldDescriptor field);

	void buildCatalogFieldQuery(StringBuilder builder, String foreignTableName, CatalogActionContext context,
			CatalogDescriptor catalogDescriptor, FieldDescriptor field);
	
	public void createForeignValueList(String foreignTableName, Long id, List<Object> fieldValue, QueryRunner runner, Logger log) throws SQLException ;

	Object handleColumnField(ResultSet rs, FieldDescriptor field, int dataType, int columnIndex, DateFormat dateFormat) throws SQLException;

}
