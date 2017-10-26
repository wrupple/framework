package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CatalogDeserializationServiceImpl implements CatalogDeserializationService {
    @Override
    public CatalogEntry deserialize(String rawSeed, CatalogDescriptor targetCatalog, CatalogActionContext context) throws Exception {
        throw new NotImplementedException();//FIXME  object mapper
    }
}
