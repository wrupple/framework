package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public class CatalogReadRequestImpl extends CatalogActionRequestImpl{
    public CatalogReadRequestImpl(Object key, String catalog) {
        super();
        super.setFollowReferences(true);
        super.setName(READ_ACTION);
        super.setEntry(key);
        super.setCatalog(catalog);
    }
}
