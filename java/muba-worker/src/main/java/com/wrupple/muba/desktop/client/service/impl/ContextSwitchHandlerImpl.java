package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.desktop.client.chain.ContextSwitchEngine;
import com.wrupple.muba.desktop.client.chain.command.ContextSwitchInterpret;
import com.wrupple.muba.desktop.domain.ContextSwitchHandler;
import com.wrupple.muba.desktop.domain.ContextSwitchManifest;
import com.wrupple.muba.event.server.service.impl.ImplicitEventResolverRegistration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContextSwitchHandlerImpl extends ImplicitEventResolverRegistration implements ContextSwitchHandler {


    @Inject
    protected ContextSwitchHandlerImpl(ContextSwitchManifest manifest, ContextSwitchEngine engine, ContextSwitchInterpret interpret) {
        super(manifest, engine, interpret, null);
    }
}
