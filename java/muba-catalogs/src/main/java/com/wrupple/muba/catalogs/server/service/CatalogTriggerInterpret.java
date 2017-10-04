package com.wrupple.muba.catalogs.server.service;

import java.util.List;
import java.util.Map;

import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;

public interface CatalogTriggerInterpret {
	void invokeTrigger(Map<String, String> properties, CatalogActionContext original, CatalogTrigger matchingRegistry) throws Exception;

	/**
	 * before == trigger.isAdvice()
	 * @param context
	 * @param advise
	 * @return
	 */
	List<CatalogActionTrigger> getTriggersValues(CatalogActionContext context, boolean advise);

    void addCatalogScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog);

    //void configureContext(CatalogActionContext userContext, CatalogTrigger matchingRegistry, Long domain, TransactionHistory transaction) throws Exception;
}
