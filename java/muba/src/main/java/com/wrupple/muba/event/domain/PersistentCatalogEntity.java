package com.wrupple.muba.event.domain;

public interface PersistentCatalogEntity extends CatalogEntry,
		HasAccesablePropertyValues {
	CatalogDescriptor getType();

	void initCatalog(CatalogDescriptor catalog);
}
