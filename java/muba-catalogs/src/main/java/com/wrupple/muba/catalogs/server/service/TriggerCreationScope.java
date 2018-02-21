package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;

public interface TriggerCreationScope {
    void add(Trigger e, CatalogDescriptor catalog, CatalogActionContext context) throws Exception;


}
