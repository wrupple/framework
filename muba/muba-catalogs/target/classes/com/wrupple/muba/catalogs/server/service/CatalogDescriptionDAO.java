package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface CatalogDescriptionDAO extends CatalogDataAccessObject<CatalogEntry> {

	CatalogDataAccessObject<? extends CatalogEntry> descriptor(CatalogDescriptor catalog);

}
