package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

public interface CatalogDeserializationService {
	CatalogEntry deserialize(String rawSeed, CatalogDescriptor targetCatalog, CatalogExcecutionContext context) throws Exception;
}
