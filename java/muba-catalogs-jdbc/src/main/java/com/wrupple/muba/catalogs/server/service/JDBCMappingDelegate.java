package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;

public interface JDBCMappingDelegate extends TableMapper, ColumnMapper {

	void createRequiredTables(CatalogActionContext context, CatalogDescriptor catalogDescriptor, QueryRunner runner, Logger log) throws SQLException;

    void createForeignValueList(String foreignTableName, Long id, List<Object> fieldValue, QueryRunner runner, Logger log) throws SQLException;

	Object handleColumnField(ResultSet rs, FieldDescriptor field, int dataType, int columnIndex, DateFormat dateFormat) throws SQLException;


}
