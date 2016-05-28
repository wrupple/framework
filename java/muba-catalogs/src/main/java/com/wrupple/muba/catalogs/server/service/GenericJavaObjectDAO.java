package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public interface GenericJavaObjectDAO<T extends CatalogEntry>
		extends CatalogDataAccessObject<T> {
	
	<V extends CatalogEntry> GenericJavaObjectDAO<V> cast(Class<V> clazz,CatalogDescriptor catalog);
	
}
