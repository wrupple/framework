package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;

public interface QueryResultHandler extends ResultSetHandler<List<CatalogEntry>> {

	void setContext(CatalogActionContext context) throws Exception;

}
