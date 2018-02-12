package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.HandleContainerState;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.worker.domain.impl.BusinessIntentImpl;

// cachuky tuku

public class HandleContainerStateImpl implements HandleContainerState {

    @Override
    public boolean execute(ContextSwitchRuntimeContext context) throws Exception {
        WorkerState worker = context.getContextSwitch().getWorkerStateValue();
        ApplicationState state = worker.getStateValue();
        BusinessIntentImpl intent = new BusinessIntentImpl();
        intent.setStateValue(state);
        intent.setDomain(state.getDomain());
        context.
                getRuntimeContext().
                getEventBus().
                fireEvent(intent
                        ,
                        context.getRuntimeContext(),
                        null
                );


        return CONTINUE_PROCESSING;
    }
}
