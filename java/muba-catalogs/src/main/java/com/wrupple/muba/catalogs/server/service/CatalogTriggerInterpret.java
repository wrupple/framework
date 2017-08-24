package com.wrupple.muba.catalogs.server.service;

import java.util.Map;

import com.wrupple.muba.event.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;

public interface CatalogTriggerInterpret {
	void invokeTrigger(Map<String, String> properties, CatalogActionContext original, CatalogTrigger matchingRegistry) throws Exception;

	void configureContext(CatalogActionContext userContext, CatalogTrigger matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
