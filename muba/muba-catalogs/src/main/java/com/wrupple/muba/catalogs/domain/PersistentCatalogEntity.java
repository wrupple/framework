package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;

public interface PersistentCatalogEntity extends CatalogEntry,
		HasAccesablePropertyValues {
	CatalogDescriptor getType();
}
