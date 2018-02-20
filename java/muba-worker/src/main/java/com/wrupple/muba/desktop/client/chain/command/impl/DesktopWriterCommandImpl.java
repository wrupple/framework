package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.worker.domain.impl.IntentImpl;

import java.util.List;

public class DesktopWriterCommandImpl implements DesktopWriterCommand {

    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {

        /*TODO vegetate channel fire load order, in this case the handler will live in the same container.

        but in gwt, web, and others the handler of the order will live in a remote instance that may not be created yet
        vegetate receives a runtime context and prints a ${tagged} html formated document

        */
        WorkerState worker = context.getWorkerState();
        worker.setRunner(context.getRequest().getRunner());

        IntentImpl intent = new IntentImpl();
        intent.setStateValue(worker.getStateValue());
        intent.setDomain(worker.getDomain());
        ApplicationState state = context.getRuntimeContext().getServiceBus().fireEvent(intent, context.getRuntimeContext(), null);
        if(state==null){
            throw new NullPointerException("Business intent resulted in no application state");
        }
        if(state.getStakeHolder()==null){
            throw new NullPointerException("No one owns this application");
        }

        //launch worker
        state.setWorkerStateValue(worker);
        worker.setStateValue(state);
        context.getRuntimeContext().getServiceBus().fireEvent(worker, context.getRuntimeContext(), null);

        return CONTINUE_PROCESSING;
    }
}
