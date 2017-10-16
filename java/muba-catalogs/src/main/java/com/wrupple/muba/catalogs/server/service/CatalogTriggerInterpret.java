package com.wrupple.muba.catalogs.server.service;

import java.util.List;
import java.util.Map;

import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import com.wrupple.muba.catalogs.domain.CatalogEventListenerImpl;
import com.wrupple.muba.catalogs.domain.UserDefinedCatalogJob;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;

public interface CatalogTriggerInterpret {
	void invokeTrigger(Map<String, String> properties, CatalogActionContext original, UserDefinedCatalogJob matchingRegistry) throws Exception;

	List<CatalogEventListener> getTriggersValues(CatalogActionContext context, boolean advise) throws Exception;

    //void addCatalogScopeTrigger(CatalogEventListener trigger, CatalogDescriptor regreso) throws Exception;

	void addNamespaceScopeTrigger(CatalogEventListener trigger, CatalogDescriptor catalog, CatalogActionContext context) throws Exception;


	//void configureContext(CatalogActionContext userContext, UserDefinedCatalogJob matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
