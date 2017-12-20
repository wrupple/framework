package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.inject.Singleton;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

@Singleton
public class CatalogEntryKeyProviderImpl implements CatalogEntryKeyProvider {

	@Override
	public Object getKey(JsCatalogEntry item) {
		if(item==null){
			return null;
		}
		/*
		 * FIXME USE THIS!!! WHEREVER POSSIBLE (to support changing of id field)
		 */
		return item.getId();
	}

}
