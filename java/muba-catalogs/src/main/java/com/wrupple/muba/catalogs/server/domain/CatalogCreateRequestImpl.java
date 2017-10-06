package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataEvent;

public class CatalogCreateRequestImpl extends CatalogActionRequestImpl{

    public CatalogCreateRequestImpl(CatalogEntry value, String ccatalog) {
        super.setCatalog(ccatalog);
        super.setName(DataEvent.CREATE_ACTION);
        super.setEntryValue(value);
    }
}
