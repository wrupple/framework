package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

public class LaunchWorkerImpl extends CatalogEntryImpl implements LaunchWorker {

    public LaunchWorkerImpl() {
        setDomain(PUBLIC_ID);
    }

    public LaunchWorkerImpl(Long domain) {
        setDomain(domain);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }


    @Override
    public Object getCatalog() {
        return getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }
}
