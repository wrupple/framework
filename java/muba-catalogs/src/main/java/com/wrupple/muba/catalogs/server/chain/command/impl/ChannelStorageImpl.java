package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.ChannelStorage;
import com.wrupple.muba.event.server.chain.ChannelCatalog;
import org.apache.commons.chain.CatalogFactory;

import javax.inject.Named;

public class ChannelStorageImpl extends CommandStorage implements ChannelStorage{


    protected ChannelStorageImpl(CatalogFactory factory,ChannelCatalog catalog) {
        super(factory, ChannelCatalog.CONTEXT_KEY);
        factory.addCatalog(ChannelCatalog.CONTEXT_KEY,catalog);

    }

    @Override
    public boolean execute(CatalogActionContext context) throws Exception {
        context.put(ChannelCatalog.CONTEXT_KEY,ChannelCatalog.CONTEXT_KEY);
        return super.execute(context);
    }
}
