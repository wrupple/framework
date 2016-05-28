package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface FieldDescriptionDAO extends CatalogDataAccessObject<CatalogEntry> {

	CatalogDataAccessObject<? extends CatalogEntry> initit(CatalogDescriptor catalog);

}
