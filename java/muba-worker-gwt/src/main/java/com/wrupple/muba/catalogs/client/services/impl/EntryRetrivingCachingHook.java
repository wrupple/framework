package com.wrupple.muba.catalogs.client.services.impl;

import com.wrupple.muba.desktop.client.services.logic.CatalogCache;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

public class EntryRetrivingCachingHook extends DataCallback<JsCatalogEntry>{

	CatalogCache cache;
	
	public EntryRetrivingCachingHook(CatalogCache cache) {
		super();
		this.cache = cache;
	}

	@Override
	public void execute() {
		if(cache!=null&&result!=null){
			cache.put(result.getId(),result);
		}
	}

}
