package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.worker.domain.WorkerLoadOrder;

public class WorkerLoadOrderImpl extends CatalogEntryImpl implements WorkerLoadOrder {

    public WorkerLoadOrderImpl() {
        setDomain(PUBLIC_ID);
    }

    public WorkerLoadOrderImpl(Long domain) {
        setDomain(domain);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }


}
