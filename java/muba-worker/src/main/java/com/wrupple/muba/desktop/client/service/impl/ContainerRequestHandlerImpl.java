package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.chain.ContainerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.ContainterRequestInterpret;
import com.wrupple.muba.desktop.client.service.ContainerRequestHandler;
import com.wrupple.muba.desktop.domain.ContainerRequestManifest;
import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;

import javax.inject.Inject;

public class ContainerRequestHandlerImpl extends ImplicitEventResolverRegistration implements ContainerRequestHandler {


    @Inject
    protected ContainerRequestHandlerImpl(ContainerRequestManifest manifest, ContainerRequestEngine engine, ContainterRequestInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
