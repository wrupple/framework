package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public class CatalogCreateRequestImpl extends CatalogActionRequestImpl{
    public CatalogCreateRequestImpl(CatalogEntry newEntry, String catalog) {
        super();
        super.setFollowReferences(true);
        super.setName(CREATE_ACTION);
        super.setEntryValue(newEntry);
        super.setCatalog(catalog);
    }
}
