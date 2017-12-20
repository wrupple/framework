package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import com.wrupple.muba.catalogs.domain.UserDefinedCatalogActionConstraint;
import com.wrupple.muba.event.domain.CatalogDescriptor;

import java.util.List;
import java.util.Map;

public interface CatalogTriggerInterpret {
    void invokeTrigger(Map<String, String> properties, CatalogActionContext original, UserDefinedCatalogActionConstraint matchingRegistry) throws Exception;

	List<CatalogEventListener> getTriggersValues(CatalogActionContext context) throws Exception;

    //void addCatalogScopeTrigger(CatalogEventListener trigger, CatalogDescriptor regreso) throws Exception;

	void addNamespaceScopeTrigger(CatalogEventListener trigger, CatalogDescriptor catalog, CatalogActionContext context) throws Exception;


    //void configureContext(CatalogActionContext userContext, UserDefinedCatalogActionConstraint matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
