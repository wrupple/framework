package com.wrupple.vegetate;

import com.google.inject.AbstractModule;
import com.wrupple.vegetate.service.RemoteBroadcastHandler;
import com.wrupple.vegetate.service.impl.RemoteBroadcastHandlerImpl;

public class VegetateModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(RemoteBroadcastHandler.class).to(RemoteBroadcastHandlerImpl.class);
    }
}
