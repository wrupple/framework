package com.wrupple.muba.catalogs.server.service;

import java.util.Map;

import com.wrupple.muba.catalogs.domain.UserDefinedCatalogJob;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;

public interface CatalogTriggerInterpret {
	void invokeTrigger(Map<String, String> properties, CatalogActionContext original, UserDefinedCatalogJob matchingRegistry) throws Exception;


    //void configureContext(CatalogActionContext userContext, UserDefinedCatalogJob matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
