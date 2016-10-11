package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogResultSet;

@Singleton
public class CatalogMetadataReadImpl implements Command {
	private static final Logger log = LoggerFactory.getLogger(CatalogMetadataReadImpl.class);

	@Inject
	public CatalogMetadataReadImpl() {
		super();

	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		String action = (String) context.get(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		Object payload;
		if (action == null) {
			log.trace("[OUTPUT CATALOG NAMES]");
			// list all domain catalogs

			payload = context.getCatalogManager().getAvailableCatalogs(context);
		} else {
			log.trace("[OUTPUT CATALOG METADATA]");
			// get full catalog descriptor
			CatalogDescriptor descriptor = context.getCatalogManager().getDescriptorForName(action, context);
			payload = descriptor;
		}
		context.put(CatalogResultSet.MULTIPLE_FOREIGN_KEY, payload);
		return CONTINUE_PROCESSING;
	}

}