package com.wrupple.muba.catalogs.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;

public interface ClientCatalogCacheManager {

	
	CatalogCache getCache(JsCatalogDescriptor catalog, JavaScriptObject properties);

	CatalogCache getIdentityCache(String foreignCatalog);

	void invalidateCache(String catalog);

	void preventInvalidation();

	void resumeInvalidation();

	boolean isInvalidationAvailable();

	void forceInvalidation(String catalog);

}
