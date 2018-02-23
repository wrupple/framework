package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;

@Singleton
public class NonOperativeCatalogReaderInterceptor implements CatalogReaderInterceptor {

@Inject
    public NonOperativeCatalogReaderInterceptor() {
    }

    @Override
	public FilterData interceptQuery(FilterData filterData, CatalogActionContext context, CatalogDescriptor catalog)
			throws Exception {
		return filterData;
	}

	@Override
	public void interceptResult(CatalogEntry originalEntry, CatalogActionContext context, CatalogDescriptor catalog) throws Exception {
		// anything requires this class is a design flaw

	}

}
