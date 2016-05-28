package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;

public interface CatalogQueryRewriter {

	FilterData rewriteFilter(FilterData filterData,
			CatalogExcecutionContext context, CatalogDescriptor catalog) throws Exception;

	void maybeBlockEntry(CatalogEntry entry,CatalogExcecutionContext context, CatalogDescriptor catalog) throws Exception;
}
