package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntryImpl;
import com.wrupple.muba.event.domain.ImplicitIntent;

/**
 * Created by japi on 12/08/17.
 */
public class ImplicitIntentImpl extends CatalogEntryImpl implements ImplicitIntent {


    private String outputCatalog,catalog;

    @Override
    public String getOutputCatalog() {
        return outputCatalog;
    }

    public void setOutputCatalog(String outputCatalog) {
        this.outputCatalog = outputCatalog;
    }

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
        return CATALOG;
    }
}
