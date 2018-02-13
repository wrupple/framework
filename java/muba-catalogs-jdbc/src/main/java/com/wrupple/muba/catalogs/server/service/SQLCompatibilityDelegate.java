package com.wrupple.muba.catalogs.server.service;

import java.sql.SQLException;

import com.wrupple.muba.catalogs.server.service.impl.JDBCMappingDelegateImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.impl.JDBCSingleLongKeyResultHandler;

public interface SQLCompatibilityDelegate {

	void alterInsertStatement(CatalogActionContext context, StringBuilder builder);

	Object getLastInsertedId(CatalogActionContext context, QueryRunner runner, JDBCSingleLongKeyResultHandler keyHandler) throws SQLException;

    boolean isSequential();

    String buildTableConfigurationStatement(JDBCMappingDelegateImpl jdbcMappingDelegate, String mainTable, CatalogDescriptor catalog, StringBuilder builder, SQLCompatibilityDelegate compatibility, CatalogActionContext context);

    boolean requiresPostCreationConfig();
}
