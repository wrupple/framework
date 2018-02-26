package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.server.chain.RemoteServiceChain;

import javax.validation.constraints.NotNull;

public class ChannelImpl  implements Channel {

    @CatalogField(ignore = true)
    private RemoteServiceChain.Link command;


    private String id;
    private Object image;
    private String  name;
    @NotNull
    private Long domain;
    private boolean anonymouslyVisible;


    public final String getId() {
        return id;
    }

    public final void setId(String catalogId) {
        this.id = catalogId;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    @Override
    public final Object getImage() {
        return image;
    }

    public final void setImage(Object image) {
        this.image = image;
    }

    public final Long getDomain() {
        return domain;
    }

    public final void setDomain(Long domain) {
        this.domain = domain;
    }

    public final boolean isAnonymouslyVisible() {
        return anonymouslyVisible;
    }

    public final void setAnonymouslyVisible(boolean anonymouslyVisible) {
        this.anonymouslyVisible = anonymouslyVisible;
    }


    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public boolean execute(RemoteServiceContext context) throws Exception {
        return getCommand().execute(context);
    }

    public RemoteServiceChain.Link getCommand() {
        return command;
    }

    public void setCommand(RemoteServiceChain.Link command) {
        this.command = command;
    }
}
