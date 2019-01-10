package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;

public class CatalogReadRequestImpl extends CatalogActionRequestImpl{

    public CatalogReadRequestImpl(Object key, String catalog) {
        super();
        super.setFollowReferences(true);
        super.setName(READ_ACTION);
        super.setEntry(key);
        super.setCatalog(catalog);
    }

    public CatalogReadRequestImpl(Object key, CatalogDescriptor catalog) {
            this( key, catalog.getDistinguishedName()) ;
            super.setCatalogValue(catalog);
        }
}
