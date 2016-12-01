package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Validator;

import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.chain.command.impl.SyntaxParsingCommang;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

@Singleton
public class CatalogRequestInterpretImpl extends SyntaxParsingCommang implements CatalogRequestInterpret {

	private final SystemCatalogPlugin cms;
	

	@Inject
	public CatalogRequestInterpretImpl(SystemCatalogPlugin cms, Validator ab, ValidationGroupProvider  ad) {
		super(ab, ad);
		this.cms = cms;
	}

	@Override
	protected Context createBlankContext(ExcecutionContext requestContext) {

		Context context = cms.spawn(requestContext);

		requestContext.setServiceContext(context);
		
		return context;
	}

	




}
