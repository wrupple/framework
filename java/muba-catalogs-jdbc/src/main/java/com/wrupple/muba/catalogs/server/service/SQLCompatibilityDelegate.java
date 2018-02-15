package com.wrupple.muba.catalogs.server.service;

import java.sql.SQLException;
import java.util.List;

import com.wrupple.muba.catalogs.server.service.impl.JDBCMappingDelegateImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.impl.JDBCSingleLongKeyResultHandler;

public interface SQLCompatibilityDelegate {

	void alterInsertStatement(CatalogActionContext context, StringBuilder builder);

	Object getLastInsertedId(CatalogActionContext context, QueryRunner runner, JDBCSingleLongKeyResultHandler keyHandler) throws SQLException;

    boolean isSequential();

    void buildTableConfigurationStatement(JDBCMappingDelegateImpl jdbcMappingDelegate, String mainTable, CatalogDescriptor catalog, StringBuilder builder, SQLCompatibilityDelegate compatibility, CatalogActionContext context, List<String> indexes);

    boolean requiresPostCreationConfig();
}
