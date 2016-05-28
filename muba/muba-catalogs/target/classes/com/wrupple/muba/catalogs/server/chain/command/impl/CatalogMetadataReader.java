package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.ContentManagementSystem;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

@Singleton
public class CatalogMetadataReader implements Command {
	private static final Logger log = LoggerFactory.getLogger(CatalogMetadataReader.class);
	private Provider<DatabasePlugin> moduleRegistryProvider;

	@Inject
	public CatalogMetadataReader(Provider<DatabasePlugin> moduleRegistryProvider) {
		super();
		this.moduleRegistryProvider = moduleRegistryProvider;

	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		String action = (String) context.get(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		Object payload;
		if (action == null) {
			log.trace("[OUTPUT CATALOG NAMES]");
			// list all domain catalogs

			payload = moduleRegistryProvider.get().getAvailableCatalogs(context);
		} else {
			log.trace("[OUTPUT CATALOG METADATA]");
			// get full catalog descriptor
			CatalogDescriptor descriptor = moduleRegistryProvider.get().getDescriptorForName(action, context);
			payload = descriptor;
		}
		context.put(ContentManagementSystem.METADATA_PAYLOAD, payload);
		return CONTINUE_PROCESSING;
	}

}