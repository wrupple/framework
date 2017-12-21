package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.LaunchContainer;
import com.wrupple.muba.desktop.client.service.LaunchWorkerHandler;
import com.wrupple.muba.desktop.domain.LaunchWorkerManifest;
import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;

import javax.inject.Inject;

public class LaunchWorkerHandlerImpl extends ImplicitEventResolverRegistration implements LaunchWorkerHandler {

    @Inject
    public LaunchWorkerHandlerImpl(LaunchWorkerManifest manifest, LaunchWorkerEngine engine, LaunchContainer interpret) {
        super(manifest, engine, interpret, null);
    }


}
