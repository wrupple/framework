package com.wrupple.muba.catalogs.server.service;

import java.util.Map;

import com.wrupple.muba.bootstrap.domain.CatalogKey;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;

public interface CatalogTriggerInterpret {
	void invokeTrigger(CatalogDescriptor catalog, CatalogKey entry, CatalogKey old,Map<String, String> properties, CatalogActionContext original, CatalogTrigger matchingRegistry) throws Exception;

	void configureContext(CatalogActionContext userContext, CatalogTrigger matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
