package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasAccesablePropertyValues;

public interface PersistentCatalogEntity extends CatalogEntry,
		HasAccesablePropertyValues {
	CatalogDescriptor getType();

	void initCatalog(CatalogDescriptor catalog);
}
