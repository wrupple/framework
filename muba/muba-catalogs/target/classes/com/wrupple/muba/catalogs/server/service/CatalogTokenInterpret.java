package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

public interface CatalogTokenInterpret {
	
	CatalogDescriptor getDescriptorForName(String catalogId,CatalogExcecutionContext context);

}
