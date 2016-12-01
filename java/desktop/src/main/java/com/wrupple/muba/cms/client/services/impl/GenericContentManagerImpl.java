package com.wrupple.muba.cms.client.services.impl;

import javax.inject.Provider;

import com.google.inject.Inject;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.cms.client.services.GenericContentManager;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class GenericContentManagerImpl extends SimpleContentManager<JsCatalogEntry> implements
		GenericContentManager {
	


	@Inject
	public GenericContentManagerImpl(
			CatalogEntryBrowserMap browserAssemblerProvider, CatalogEditorMap editorMap, Provider<TransactionalActivityAssembly> processAssemblyProvider, DesktopTheme theme) {
		super(null,  theme, browserAssemblerProvider, editorMap, processAssemblyProvider);
	}

	@Override
	public void setCatalogId(String managedCatalog) {
		super.managedCatalog=managedCatalog;
	}


	

}
