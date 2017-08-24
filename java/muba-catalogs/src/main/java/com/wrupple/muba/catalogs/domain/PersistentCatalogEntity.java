package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.HasAccesablePropertyValues;

public interface PersistentCatalogEntity extends CatalogEntry,
		HasAccesablePropertyValues {
	CatalogDescriptor getType();

	void initCatalog(CatalogDescriptor catalog);
}
