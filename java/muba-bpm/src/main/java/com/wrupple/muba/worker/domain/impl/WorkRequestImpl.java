package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.worker.domain.WorkRequest;

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


