package com.wrupple.vegetate.server.service.impl;

import com.google.inject.name.Named;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.catalogs.server.service.impl.StaticCatalogDescriptorProvider;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.impl.ChannelImpl;
import com.wrupple.muba.event.server.chain.ChannelCatalog;
import com.wrupple.muba.event.server.chain.RemoteServiceChain;
import com.wrupple.vegetate.chain.command.ChannelStorage;
import com.wrupple.vegetate.chain.command.LocalServiceDelegation;
import com.wrupple.vegetate.server.service.VegetateCatalogPlugin;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class VegetateCatalogPluginImpl   extends StaticCatalogDescriptorProvider implements VegetateCatalogPlugin {

    @Inject
    public VegetateCatalogPluginImpl(@Named(RemoteBroadcast.CATALOG) CatalogDescriptor remoteBroadcasst,
                                     @Named(ChannelAgreement.CATALOG) CatalogDescriptor channelAgreed,
                                     @Named(Channel.CATALOG) CatalogDescriptor channel,
                                     ChannelStorage channelStorage,
                                     CatalogFactory factory,
                                     ChannelCatalog catalog,
                                     LocalServiceDelegation localDelegation,
     QueryReaders queryers, PrimaryKeyReaders primaryKeyers) {
        super.put(remoteBroadcasst);
        super.put(channelAgreed);
        super.put(channel);
        factory.addCatalog(ChannelCatalog.CONTEXT_KEY,catalog);
        addChannel(LocalServiceDelegation.class.getSimpleName(),localDelegation,catalog);

        queryers.addCommand(ChannelStorage.class.getSimpleName(),channelStorage);
        primaryKeyers.addCommand(ChannelStorage.class.getSimpleName(),channelStorage);

    }

    private void addChannel(String name, RemoteServiceChain.Link command, ChannelCatalog catalog) {
        ChannelImpl channel = new ChannelImpl();
        channel.setId(name);
        channel.setName(name);
        channel.setCommand(command);
        catalog.addCommand(channel.getId(),channel);
    }

    @Override
    public ValidationExpression[] getValidations() {
        return null;
    }

    @Override
    public Command[] getCatalogActions() {
        return new Command[0];
    }

    @Override
    public void postProcessCatalogDescriptor(CatalogDescriptor c, CatalogActionContext context, TriggerCreationScope scope) throws Exception {

    }
}
