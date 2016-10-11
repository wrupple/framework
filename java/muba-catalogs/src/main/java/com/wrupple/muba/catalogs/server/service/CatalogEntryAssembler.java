package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionResult;
import com.wrupple.muba.catalogs.domain.CatalogColumnResultSet;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

public interface CatalogEntryAssembler {
	public <T extends CatalogEntry> List<T> processResultSet(CatalogColumnResultSet resultSet, CatalogDescriptor catalogid)  throws Exception;
	public List<CatalogEntry> processMultipleResponse(CatalogActionResult	 response, CatalogDescriptor catalogdescriptor) throws Exception;
	public CatalogEntry processSingleResponse(CatalogActionResult response, CatalogDescriptor catalogdescriptor) throws Exception ;
}
