package com.wrupple.muba.catalogs.server.service;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.impl.JDBCSingleLongKeyResultHandler;

public interface SQLCompatibilityDelegate {

	void alterInsertStatement(CatalogActionContext context, StringBuilder builder);

	Object getLastInsertedId(CatalogActionContext context, QueryRunner runner, JDBCSingleLongKeyResultHandler keyHandler) throws SQLException;

    boolean isSequential();
}
