package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.chain.WorkerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.WorkerRequestInterpret;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;

import javax.inject.Inject;

public class WorkerRequestHandlerImpl extends ImplicitEventResolverRegistration implements WorkerRequestHandler {


    @Inject
    protected WorkerRequestHandlerImpl(WorkerRequestManifest manifest, WorkerRequestEngine engine, WorkerRequestInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
