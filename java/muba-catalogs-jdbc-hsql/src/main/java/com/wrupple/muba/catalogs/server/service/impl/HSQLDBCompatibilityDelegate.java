package com.wrupple.muba.catalogs.server.service.impl;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;

public class HSQLDBCompatibilityDelegate implements SQLCompatibilityDelegate {

	
	private static final String LAST_ID = "CALL IDENTITY()";
	@Override
	public void alterInsertStatement(CatalogActionContext context, StringBuilder builder) {

	}


	@Override
	public Object getLastInsertedId(CatalogActionContext context, QueryRunner runner,
			JDBCSingleLongKeyResultHandler keyHandler) throws SQLException {
		return runner.query(LAST_ID, keyHandler);
	}

	@Override
	public boolean isSequential() {
		return true;
	}

}
