package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogDescriptorUpdateTriggerImpl implements CatalogDescriptorUpdateTrigger {

	private final CatalogResultCache cache;
	
	@Inject
	public CatalogDescriptorUpdateTriggerImpl(CatalogResultCache cache) {
		this.cache=cache;
	}
	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		
		cache.put(context, SystemCatalogPlugin.DOMAIN_METADATA, null);
		
		return CONTINUE_PROCESSING;
	}

}
