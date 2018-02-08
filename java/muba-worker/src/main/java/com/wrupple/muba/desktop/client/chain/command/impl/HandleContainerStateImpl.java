package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.domain.ContainerContext;

// cachuky tuku

public class HandleContainerStateImpl implements HandleContainerState {

    @Override
    public boolean execute(ContainerContext context) throws Exception {
        context.
                getRuntimeContext().
                getEventBus().
                fireEvent(
                        context.getContextSwitch().getState(),
                        context.getRuntimeContext(),
                        null
                );


        return CONTINUE_PROCESSING;
    }
}
