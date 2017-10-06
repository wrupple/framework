package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.WorkRequest;

/**
 * Created by japi on 12/08/17.
 */
public class WorkRequestImpl extends BusinessIntentImpl implements WorkRequest {
    @Override
    public String getOutputCatalog() {
        return outputCatalog;
    }

    public void setOutputCatalog(String outputCatalog) {
        this.outputCatalog = outputCatalog;
    }

    private String outputCatalog;

    @Override
    public String getCatalogType() {
        return WorkRequest.CATALOG;
    }
}


