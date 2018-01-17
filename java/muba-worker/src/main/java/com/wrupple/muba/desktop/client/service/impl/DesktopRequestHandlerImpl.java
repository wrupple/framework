package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.chain.DesktopEngine;
import com.wrupple.muba.desktop.client.chain.command.WorkerRequestInterpret;
import com.wrupple.muba.desktop.client.service.DesktopRequestHandler;
import com.wrupple.muba.desktop.domain.DesktopServiceManifest;
import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;

import javax.inject.Inject;

public class DesktopRequestHandlerImpl extends ImplicitEventResolverRegistration implements DesktopRequestHandler {


    @Inject
    protected DesktopRequestHandlerImpl(DesktopServiceManifest manifest, DesktopEngine engine, WorkerRequestInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
