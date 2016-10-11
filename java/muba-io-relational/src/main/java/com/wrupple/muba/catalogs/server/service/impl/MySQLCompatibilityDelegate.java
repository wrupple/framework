package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Singleton;

import org.apache.commons.dbutils.QueryRunner;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;

@Singleton
public class MySQLCompatibilityDelegate implements SQLCompatibilityDelegate {

	@Override
	public void alterInsertStatement(CatalogActionContext context, StringBuilder builder) {

	}

	@Override
	public Object getLastInsertedId(CatalogActionContext context, QueryRunner runner,
			JDBCSingleLongKeyResultHandler keyHandler) {
		// MySQL returns last inserted ID to result handler in latest tests
		return null;
	}


}
