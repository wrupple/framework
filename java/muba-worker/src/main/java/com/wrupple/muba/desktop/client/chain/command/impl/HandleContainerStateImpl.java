package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.WorkerState;

// cachuky tuku

public class HandleContainerStateImpl implements HandleContainerState {

    @Override
    public boolean execute(ContextSwitchRuntimeContext context) throws Exception {
        WorkerState worker = context.getContextSwitch().getWorkerStateValue();
        ApplicationState state = worker.getStateValue();


        //FIXME fire inderectly through handler
        context.
                getRuntimeContext().
                getServiceBus().
                fireEvent(state
                        ,
                        context.getRuntimeContext(),
                        null
                );


        return CONTINUE_PROCESSING;
    }
}
