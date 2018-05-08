package com.wrupple.muba.catalogs.server.service;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;

public interface CatalogDescriptorService {

    CatalogDescriptor getDescriptorForKey(Long numericId, CatalogActionContext context) throws Exception;

    CatalogDescriptor getDescriptorForName(String catalogId, CatalogActionContext context) throws Exception;

}
