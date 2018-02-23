package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

public class RemoteBroadcastImpl extends CatalogEntryImpl implements RemoteBroadcast {
    private BroadcastEvent queuedElementValue;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Host.CATALOG)
    private Host hostValue;
    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long host;


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

    @Override
    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }
}
