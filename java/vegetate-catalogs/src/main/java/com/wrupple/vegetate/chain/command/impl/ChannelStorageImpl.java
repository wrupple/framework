package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.impl.CommandStorage;
import com.wrupple.muba.event.server.chain.ChannelCatalog;
import com.wrupple.vegetate.chain.command.ChannelStorage;
import org.apache.commons.chain.CatalogFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelStorageImpl extends CommandStorage implements ChannelStorage {


    @Inject
    public ChannelStorageImpl(CatalogFactory factory) {
        super(factory, ChannelCatalog.CONTEXT_KEY);
    }

    @Override
    public boolean execute(CatalogActionContext context) throws Exception {
        context.put(ChannelCatalog.CONTEXT_KEY,ChannelCatalog.CONTEXT_KEY);
        return super.execute(context);
    }
}
