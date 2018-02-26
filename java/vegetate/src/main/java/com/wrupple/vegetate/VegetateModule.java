package com.wrupple.vegetate;

import com.google.inject.AbstractModule;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.muba.event.domain.impl.RemoteServiceContextImpl;
import com.wrupple.muba.event.server.chain.ChannelCatalog;
import com.wrupple.muba.event.server.chain.RemoteServiceChain;
import com.wrupple.vegetate.chain.command.AssignChannel;
import com.wrupple.vegetate.chain.command.LocalServiceDelegation;
import com.wrupple.vegetate.chain.command.RemoteServiceInterpret;
import com.wrupple.vegetate.chain.command.Send;
import com.wrupple.vegetate.chain.command.impl.*;
import com.wrupple.vegetate.domain.RemoteServiceManifest;
import com.wrupple.vegetate.domain.impl.RemoteServiceManifestImpl;
import com.wrupple.vegetate.service.RemoteBroadcastHandler;
import com.wrupple.vegetate.service.impl.ChannelCatalogImpl;
import com.wrupple.vegetate.service.impl.RemoteBroadcastHandlerImpl;

public class VegetateModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(RemoteBroadcastHandler.class).to(RemoteBroadcastHandlerImpl.class);
        bind(RemoteServiceManifest.class).to(RemoteServiceManifestImpl.class);
        bind(RemoteServiceChain.class).to(RemoteServiceChainImpl.class);
        bind(RemoteServiceInterpret.class).to(RemoteServiceInterpretImpl.class);
        bind(RemoteServiceContext.class).to(RemoteServiceContextImpl.class);

        bind(AssignChannel.class).to(AssignChannelImpl.class);
        bind(Send.class).to(SendImpl.class);
        bind(ChannelCatalog.class).to(ChannelCatalogImpl.class);
        bind(LocalServiceDelegation.class).to(LocalServiceDelegationImpl.class);
    }
}
