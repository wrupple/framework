package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;

public interface PersistentCatalogEntity extends CatalogEntry,
        HasAccesablePropertyValues {
	String IMAGE_FIELD = "image";
	CatalogDescriptor getType();

	void initCatalog(CatalogDescriptor catalog);
}
