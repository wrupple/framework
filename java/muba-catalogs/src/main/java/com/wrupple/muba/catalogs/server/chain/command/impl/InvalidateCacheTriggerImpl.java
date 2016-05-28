package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptionCacheManager;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.HasCatalogId;

@Singleton
public class InvalidateCacheTriggerImpl implements CatalogDescriptorUpdateTrigger {
	
	private final CatalogDescriptionCacheManager cache;
	
	@Inject
	public InvalidateCacheTriggerImpl(CatalogDescriptionCacheManager cache) {
		this.cache=cache;
	}


	@Override
	public boolean execute(Context context) throws Exception {
		HasCatalogId e=(HasCatalogId) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		if(e==null){
			e=(HasCatalogId) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		}
		cache.removeKey((String)e.getCatalogId(), (CatalogExcecutionContext) context);
		return CONTINUE_PROCESSING;
	}


}
