package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.event.domain.CatalogDescriptor;

public class StorageTriggerScope implements TriggerCreationScope {

    @Override
    public void add(Trigger trigger, CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
        trigger.setCatalog(catalog.getDistinguishedName());
        context.triggerCreate(Trigger.CATALOG,trigger);
    }
}
