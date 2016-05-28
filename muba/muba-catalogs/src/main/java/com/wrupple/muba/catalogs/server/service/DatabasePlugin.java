package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

public interface DatabasePlugin extends CatalogPlugin {

	List<CatalogIdentification> getAvailableCatalogs(CatalogExcecutionContext context) throws Exception;

	CatalogPlugin[] getPlugins();

}
