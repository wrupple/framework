package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;

import com.wrupple.muba.catalogs.server.chain.command.CommitCatalogAction;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
@Singleton
public final class CommitCatalogActionImpl extends LookupCommand implements CommitCatalogAction {
	private Provider<CatalogMetadataReader> mdataReaderp;

	@Inject
	public CommitCatalogActionImpl(CatalogFactory commandDictionatyFactory,Provider<CatalogMetadataReader> mdataReaderp) {
		super(commandDictionatyFactory);
		this.mdataReaderp=mdataReaderp;
		super.setCatalogName(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
		super.setNameKey(CatalogActionRequest.CATALOG_ACTION_PARAMETER);
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		
		if (CatalogActionRequest.LIST_ACTION_TOKEN.equals(context.getCatalog())) {
			
			return mdataReaderp.get().execute(context);
		}
		
		Set<ConstraintViolation<?>> violations = context.getConstraintViolations();
		if (violations == null || violations.isEmpty()) {
			super.execute(context);

		}
		return CONTINUE_PROCESSING;
	}
}