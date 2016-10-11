package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogCommand;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;

@Singleton
public  class CatalogCommandImpl extends LookupCommand  implements CatalogCommand {

	protected static final Logger log = LoggerFactory.getLogger(CatalogCommandImpl.class);
	protected final CatalogEvaluationDelegate accessor;
	
	private final CatalogMetadataReadImpl mdataReader;

	
	@Inject
	public CatalogCommandImpl(CatalogMetadataReadImpl mdataReader, CatalogEvaluationDelegate accessor, CatalogFactory factory) {
		super(factory);
		this.accessor = accessor;
		this.mdataReader=mdataReader;
		super.setCatalogName(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		super.setNameKey(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
	}


	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		
		if (CatalogActionRequest.LIST_ACTION_TOKEN.equals(context.getCatalog())) {
			return mdataReader.execute(context);
		}
		
		Set<ConstraintViolation<?>> violations = context.getConstraintViolations();
		if (violations == null || violations.isEmpty()) {
			return	super.execute(context);
		}else{
			return  CONTINUE_PROCESSING;
		}
		
	}



}
