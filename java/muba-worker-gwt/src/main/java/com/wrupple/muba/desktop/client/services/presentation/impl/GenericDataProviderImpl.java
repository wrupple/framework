package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.inject.Inject;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.vegetate.client.services.StorageManager;

public class GenericDataProviderImpl extends
		CompositeDataProvider<JsCatalogEntry> implements GenericDataProvider {

	@Inject
	public GenericDataProviderImpl(ClientCatalogCacheManager ccm,DesktopManager dm,
			StorageManager storageManager,
			CatalogEntryKeyProvider keyprovider) {
		super(ccm, dm,storageManager, keyprovider);
	}

	
}
