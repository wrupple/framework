package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;

import javax.inject.Inject;

public class BroadcastEventHandlerImpl extends ImplicitEventResolverRegistration {

    @Inject
    public BroadcastEventHandlerImpl(BroadcastServiceManifest manifest, PublishEvents engine, BroadcastInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
