package com.wrupple.muba.cms.client.services.impl;

import javax.inject.Provider;
import javax.inject.Singleton;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.cms.client.services.GenericContentManager;
import com.wrupple.muba.desktop.client.factory.dictionary.ContentManagerMap;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

@Singleton
public class GenericContentManagementSystem implements
ContentManagementSystem {

	Provider<GenericContentManager> provider;
	private ContentManagerMap cmsRegistry;

	@Inject
	public GenericContentManagementSystem(
			Provider<GenericContentManager> provider,ContentManagerMap cmsRegistry) {
		super();
		this.provider = provider;
		this.cmsRegistry=cmsRegistry;
	}



	
	@Override
	public ContentManager<JsCatalogEntry> getContentManager(String catalog) {
		ContentManager<? extends JavaScriptObject> cms;
		try{
			cms =   cmsRegistry.get(catalog);
			if(cms==null){
				GenericContentManager gcms = this.provider.get();
				gcms.setCatalogId(catalog);
				cms = gcms;
			}
		}catch(Throwable  e){
			GenericContentManager gcms = this.provider.get();
			gcms.setCatalogId(catalog);
			cms = gcms;
		}
		return (ContentManager<JsCatalogEntry>) cms;
	}

	
}
