package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.wrupple.muba.bootstrap.server.service.EventBus;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.bpm.server.service.impl.BusinessPluginImpl;
import com.wrupple.muba.bpm.server.service.impl.EventBusImpl;

/**
 * Created by japi on 11/08/17.
 */
public class BusinessModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventBus.class).to(EventBusImpl.class);
        bind(BusinessPlugin.class).to(BusinessPluginImpl.class);
    }
}
