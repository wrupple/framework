package com.wrupple.muba.catalogs.server.service.impl;

import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogReaderInterceptor;

@Singleton
public class NonOperativeCatalogReaderInterceptor implements CatalogReaderInterceptor {

	@Override
	public FilterData interceptQuery(FilterData filterData, CatalogActionContext context, CatalogDescriptor catalog)
			throws Exception {
		return filterData;
	}

	@Override
	public void interceptResult(CatalogEntry entry, CatalogActionContext context, CatalogDescriptor catalog)
			throws Exception {

	}

}
