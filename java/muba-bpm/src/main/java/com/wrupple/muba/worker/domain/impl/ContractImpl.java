package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

/**
 * Created by japi on 12/08/17.
 */
public class ContractImpl extends CatalogEntryImpl implements Contract {


    private String catalog;

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    @Override
    public String getCatalogType() {
        return Event_CATALOG;
    }
}
