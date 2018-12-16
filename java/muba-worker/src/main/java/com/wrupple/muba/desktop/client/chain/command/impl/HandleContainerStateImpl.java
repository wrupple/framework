package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.WorkerState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// cachuky tuku

public class HandleContainerStateImpl implements HandleContainerState {
    protected Logger log = LogManager.getLogger(HandleContainerStateImpl.class);

    @Override
    public boolean execute(ContextSwitchRuntimeContext context) throws Exception {

        WorkerState worker = context.getContextSwitch().getWorkerStateValue();
        ApplicationState state = worker.getStateValue();
        state.setWorkerStateValue(worker);
        //subsequent updates are handled via Catalog Events
        log.info("fire application state...");
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
