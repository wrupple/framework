package com.wrupple.muba.cms.client.services;

import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public interface ContentManagementSystem {
	
	ContentManager<JsCatalogEntry> getContentManager(String catalog);
	
}
