package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.server.chain.RemoteServiceChain;

public class ChannelImpl extends CatalogEntryImpl implements Channel {

    @CatalogField(ignore = true)
    private RemoteServiceChain.Link command;

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
