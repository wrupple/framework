package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.chain.command.impl.PathParsingCommand;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.CatalogManager;

@Singleton
public class CatalogRequestInterpretImpl extends PathParsingCommand implements CatalogRequestInterpret {

	private final CatalogManager cms;

	@Inject
	public CatalogRequestInterpretImpl(CatalogManager cms) {
		super();
		this.cms = cms;
	}

	@Override
	protected Context createBlankContext(ExcecutionContext requestContext) {

		Context context = cms.spawn(requestContext);

		requestContext.setServiceContext(context);
		
		return context;
	}

}
