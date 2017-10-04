package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.server.chain.command.CommitCatalogAction;

@Singleton
public class CommitCatalogActionImpl extends LookupCommand implements CommitCatalogAction {


	@Inject
	public CommitCatalogActionImpl(CatalogFactory factory) {
		super(factory);
		super.setCatalogName(CatalogActionRequest.NAME_FIELD);
		super.setNameKey(CatalogActionRequest.NAME_FIELD);
	}


}
