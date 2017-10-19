package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.BroadcastQueueAppend;
import com.wrupple.muba.event.domain.Host;

public class BroadcastQueueAppendImpl extends CatalogEntryImpl implements BroadcastQueueAppend {
    private BroadcastEvent queuedElementValue;
    private Host hostValue;
    private String catalog;

    public BroadcastEvent getQueuedElementValue() {
        return queuedElementValue;
    }

    @Override
    public void setQueuedElementValue(BroadcastEvent queuedElementValue) {
        this.queuedElementValue = queuedElementValue;
    }

    public Host getHostValue() {
        return hostValue;
    }

    @Override
    public void setHostValue(Host hostValue) {
        this.hostValue = hostValue;
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
