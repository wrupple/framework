package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class CatalogEngineImpl extends LookupCommand implements CatalogEngine {


	@Inject
    public CatalogEngineImpl(CatalogFactory factory) {
        super(factory);
        super.setCatalogName(CatalogActionRequest.NAME_FIELD);
        super.setNameKey(CatalogActionRequest.NAME_FIELD);
    }


}
