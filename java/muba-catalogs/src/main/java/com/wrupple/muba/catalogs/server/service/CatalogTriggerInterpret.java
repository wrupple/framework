package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.UserDefinedCatalogActionConstraint;
import com.wrupple.muba.event.domain.CatalogDescriptor;

import java.util.List;
import java.util.Map;

public interface CatalogTriggerInterpret {
    void invokeTrigger(Map<String, String> properties, CatalogActionContext original, UserDefinedCatalogActionConstraint matchingRegistry) throws Exception;

	List<Trigger> getTriggersValues(CatalogActionContext context) throws Exception;

}
