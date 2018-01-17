package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import org.apache.commons.chain.Context;

import javax.inject.Singleton;

@Singleton
public class LaunchApplicationStateImpl implements LaunchApplicationState {


    @Override
    public boolean execute(Context ctx) throws Exception {

        // this context is universally available through WorkerContainerLauncher.getContainer()
        ContainerContext context = (ContainerContext) ctx;

        ContextSwitch order = context.getContextSwitch();

        context.getRuntimeContext().getEventBus().fireEvent(order, context.getRuntimeContext(), null);

        return CONTINUE_PROCESSING;
    }

}
