package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.InstallActivityEventHandler;
import com.wrupple.muba.desktop.domain.ContainerContext;

public class InstallActivityEventHandlerImpl implements InstallActivityEventHandler {

    private final ActivityVegetateEventHandler vegetateHandler;


    @Override
    public boolean execute(ContainerContext context) throws Exception {

        callback.hook(vegetateHandler);
        eventBus.addHandler(VegetateEvent.TYPE, vegetateHandler);


        return CONTINUE_PROCESSING;
    }
}
