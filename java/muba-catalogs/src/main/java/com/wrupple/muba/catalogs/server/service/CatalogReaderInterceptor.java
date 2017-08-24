package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

public interface CatalogReaderInterceptor {

	FilterData interceptQuery(FilterData filterData,
			CatalogActionContext context, CatalogDescriptor catalog) throws Exception;

	void interceptResult(CatalogEntry entry,CatalogActionContext context, CatalogDescriptor catalog) throws Exception;
}
