package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

public interface CatalogDeserializationService {
	CatalogEntry deserialize(String rawSeed, CatalogDescriptor targetCatalog, CatalogActionContext context) throws Exception;
}
