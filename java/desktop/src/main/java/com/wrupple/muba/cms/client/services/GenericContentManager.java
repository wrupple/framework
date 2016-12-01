package com.wrupple.muba.cms.client.services;

import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface GenericContentManager extends
		ContentManager<JsCatalogEntry> {
	public void setCatalogId(String managedCatalog);
}
