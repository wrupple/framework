package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.HasAccesablePropertyValues;

public interface PropertyMapDAO<T extends HasAccesablePropertyValues> extends
		CatalogDataAccessObject<T> {
	
	void setCatalogDescriptor(CatalogDescriptor catalog);

	void overridePersistentKind(String domainRegistryEntity);

	void setNamespace();

	void unsetNamespace();

}
