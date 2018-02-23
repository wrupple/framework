package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.FilterData;

public class CatalogQueryRequestImpl extends CatalogActionRequestImpl{
    public CatalogQueryRequestImpl(FilterData query, String catalog) {
        super();
        super.setFollowReferences(true);
        super.setName(READ_ACTION);
        super.setFilter(query);
        super.setCatalog(catalog);
    }
}
