package com.wrupple.muba.catalogs.server.service;

import java.util.List;

import com.wrupple.muba.catalogs.domain.VegetateColumnResultSet;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface CatalogEntryAssembler {
	public <T extends CatalogEntry> List<T> processResultSet(VegetateColumnResultSet resultSet, CatalogDescriptor catalogid)  throws Exception;
}
