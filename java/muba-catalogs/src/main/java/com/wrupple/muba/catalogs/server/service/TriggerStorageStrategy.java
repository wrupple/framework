package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.event.domain.CatalogDescriptor;

import java.util.List;

public interface TriggerStorageStrategy {


    /**
     * before == trigger.isAdvice()
     * @param context
     * @param advise
     * @return
     */
    List<CatalogActionTrigger> getTriggersValues(CatalogActionContext context, boolean advise);

    void addCatalogScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog);

    void addNamespaceScopeTrigger(CatalogActionTrigger trigger, CatalogDescriptor catalog, CatalogActionContext context);
}
