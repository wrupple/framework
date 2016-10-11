package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.HasCatalogId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogManager;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;

@Singleton
public class InvalidateCacheTriggerImpl implements CatalogDescriptorUpdateTrigger {
	
	private final CatalogResultCache cache;
	
	@Inject
	public InvalidateCacheTriggerImpl(CatalogResultCache cache) {
		this.cache=cache;
	}


	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		List<HasCatalogId> e= (List)context.getResults();
		if(e==null){
			e=(List)context.getResults();
		}
		for(HasCatalogId i :e){
			cache.delete(context, CatalogManager.DOMAIN_METADATA, i.getCatalog());
		}
		
		return CONTINUE_PROCESSING;
	}


}
