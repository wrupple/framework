package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.server.chain.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptionCacheManager;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

@Singleton
public class FieldDescriptorUpdateTriggerImpl implements FieldDescriptorUpdateTrigger {

	private final Provider<DatabasePlugin> moduleRegistryProvider;
	private final CatalogDescriptionCacheManager cache;
	
	@Inject
	public FieldDescriptorUpdateTriggerImpl(Provider<DatabasePlugin> moduleRegistryProvider,CatalogDescriptionCacheManager cache) {
		this.moduleRegistryProvider=moduleRegistryProvider;
		this.cache=cache;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		//TODO read catalog's owned fields and ¡¡ONLY!! if updated field is owned invalidate catalog descriptor cache
		CatalogEntry updatedField=(CatalogEntry) c.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
		if(updatedField==null){
			updatedField=(CatalogEntry) c.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		}
		
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		
		List<CatalogIdentification> names = moduleRegistryProvider.get().getAvailableCatalogs(context);
		
		for(CatalogIdentification key: names){
			cache.removeKey(key.getIdAsString(), context);
		}
		
		return CONTINUE_PROCESSING;
	}

}
