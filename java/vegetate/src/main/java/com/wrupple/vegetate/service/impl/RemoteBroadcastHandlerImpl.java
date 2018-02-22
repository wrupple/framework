package com.wrupple.vegetate.service.impl;

import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;
import com.wrupple.vegetate.chain.RemoteServiceChain;
import com.wrupple.vegetate.chain.command.RemoteServiceInterpret;
import com.wrupple.vegetate.domain.RemoteServiceManifest;
import com.wrupple.vegetate.service.RemoteBroadcastHandler;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RemoteBroadcastHandlerImpl extends ImplicitEventResolverRegistration implements RemoteBroadcastHandler {

    @Inject
    public RemoteBroadcastHandlerImpl(RemoteServiceManifest manifest, RemoteServiceChain engine, RemoteServiceInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
