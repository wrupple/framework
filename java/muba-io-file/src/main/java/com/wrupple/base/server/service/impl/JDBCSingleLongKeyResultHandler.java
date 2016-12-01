package com.wrupple.base.server.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbutils.ResultSetHandler;

public class JDBCSingleLongKeyResultHandler implements ResultSetHandler<Long> {

	public JDBCSingleLongKeyResultHandler() {
	}

	@Override
	public Long handle(ResultSet rs) throws SQLException {
		if(rs.next()){
			return rs.getLong(1);
		}
		return null;
	}

}
