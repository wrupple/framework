package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.vegetate.domain.CatalogDescriptor;

public interface PersistentCatalogEntityFactory {

	PersistentCatalogEntity newEntity(CatalogDescriptor type);

}
