package com.wrupple.vegetate;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.domain.impl.ChannelAgreementImpl;
import com.wrupple.muba.event.domain.impl.ChannelImpl;
import com.wrupple.muba.event.domain.impl.RemoteBroadcastImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.vegetate.chain.command.ChannelStorage;
import com.wrupple.vegetate.server.service.VegetateCatalogPlugin;
import com.wrupple.vegetate.server.service.impl.VegetateCatalogPluginImpl;

import java.util.Arrays;

public class VegetateCatalogsModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(VegetateCatalogPlugin.class).to(VegetateCatalogPluginImpl.class);
    }


    @Provides
    @Singleton
    @Inject
    @Named(RemoteBroadcast.CATALOG)
    public CatalogDescriptor broadcast(CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(RemoteBroadcastImpl.class, RemoteBroadcast.CATALOG, "Remote Broadcast", -2919,
                null);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(ChannelAgreement.CATALOG)
    public CatalogDescriptor agreement(CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ChannelAgreementImpl.class, ChannelAgreement.CATALOG, "Channel Agreement", -2918,
                null);
        return r;
    }

    @Provides
    @Singleton
    @Inject
    @Named(Channel.CATALOG)
    public CatalogDescriptor channel(CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ChannelImpl.class, Channel.CATALOG, "Channel ", -2917,
                null);
        r.setStorage(Arrays.asList(ChannelStorage.class.getSimpleName()));

        return r;
    }
}
