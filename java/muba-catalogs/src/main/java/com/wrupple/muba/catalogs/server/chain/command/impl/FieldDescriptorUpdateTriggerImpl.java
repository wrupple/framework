package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

@Singleton
public class FieldDescriptorUpdateTriggerImpl implements FieldDescriptorUpdateTrigger {

	private final CatalogResultCache cache;
	
	@Inject
	public FieldDescriptorUpdateTriggerImpl(CatalogResultCache cache) {
		this.cache=cache;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		
		//TODO read catalog's owned fields and ¡¡ONLY!! if updated field is owned invalidate catalog descriptor cache
		CatalogEntry updatedField=context.getOldValue();
		if(updatedField==null){
			updatedField=context.getEntryResult();
		}
		
		
		
		List<CatalogEntry> names = context.getAvailableCatalogs();
		
		for(CatalogEntry key: names){
			cache.delete(context, SystemCatalogPlugin.DOMAIN_METADATA,key.getId());
		}
		
		return CONTINUE_PROCESSING;
	}

}
