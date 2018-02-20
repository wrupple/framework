package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InvalidateCacheTriggerImpl implements CatalogDescriptorUpdateTrigger {
    protected static final Logger log = LoggerFactory.getLogger(DataJoiner.class);

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
			cache.delete(context, SystemCatalogPlugin.DOMAIN_METADATA, i.getCatalog());
		}

		return CONTINUE_PROCESSING;
	}


}
