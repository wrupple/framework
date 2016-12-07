package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.chain.command.CommitCatalogAction;

@Singleton
public class CommitCatalogActionImpl extends LookupCommand implements CommitCatalogAction {


	@Inject
	public CommitCatalogActionImpl(CatalogFactory factory) {
		super(factory);
		super.setCatalogName(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		super.setNameKey(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
	}


}
