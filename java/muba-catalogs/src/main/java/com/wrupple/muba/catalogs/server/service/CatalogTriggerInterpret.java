package com.wrupple.muba.catalogs.server.service;

import java.util.Map;

import com.wrupple.muba.catalogs.server.service.impl.CatalogUserTransaction;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.CatalogTrigger;

public interface CatalogTriggerInterpret {
	void invokeTrigger(CatalogDescriptor catalog, CatalogKey entry, CatalogKey old,Map<String, String> properties, CatalogExcecutionContext original, CatalogTrigger matchingRegistry) throws Exception;

	void configureContext(CatalogExcecutionContext userContext, CatalogTrigger matchingRegistry, Long domain, CatalogUserTransaction transaction) throws Exception;
}
