package com.wrupple.muba.event.domain.impl;

public class CatalogReadRequestImpl extends CatalogActionRequestImpl{
    public CatalogReadRequestImpl(Object key, String catalog) {
        super();
        super.setFollowReferences(true);
        super.setName(READ_ACTION);
        super.setEntry(key);
        super.setCatalog(catalog);
    }
}
