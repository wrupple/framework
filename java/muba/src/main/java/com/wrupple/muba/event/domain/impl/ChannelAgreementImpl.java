package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

import java.util.List;

public class ChannelAgreementImpl extends CatalogEntryImpl implements ChannelAgreement {

    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long host;

    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Host.CATALOG)
    private Host hostValue;

    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Channel.CATALOG)
    private Channel channelValue;

    @ForeignKey(foreignCatalog = Channel.CATALOG)
    private String channel;


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

    @Override
    public Host getHostValue() {
        return hostValue;
    }

    public void setHostValue(Host hostValue) {
        this.hostValue = hostValue;
    }

    @Override
    public Channel getChannelValue() {
        return channelValue;
    }

    public void setChannelValue(Channel channelValue) {
        this.channelValue = channelValue;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
