package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;

// cachuky tuku

public class HandleContainerStateImpl implements HandleContainerState {

    @Override
    public boolean execute(ContextSwitchRuntimeContext context) throws Exception {
        context.
                getRuntimeContext().
                getEventBus().
                fireEvent(
                        context.getContextSwitch().getStateValue(),
                        context.getRuntimeContext(),
                        null
                );


        return CONTINUE_PROCESSING;
    }
}
