package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.worker.domain.impl.IntentImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class DesktopWriterCommandImpl implements DesktopWriterCommand {
    protected Logger log = LogManager.getLogger(DesktopWriterCommandImpl.class);

    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {

        /*TODO vegetate channel fire load order, in this case the handler will live in the same container.

        but in gwt, web, and others the handler of the order will live in a remote instance that may not be created yet
        vegetate receives a runtime context and prints a ${tagged} html formated document

        */
        WorkerState worker = context.getWorkerState();
        ApplicationState state=  worker.getStateValue();
        state.setWorkerStateValue(worker);

        log.info("Packing worker state into an intention");
        IntentImpl intent = new IntentImpl();
        intent.setStateValue(state);
        intent.setDomain(worker.getDomain());

        state= context.getRuntimeContext().getServiceBus().fireEvent(intent, context.getRuntimeContext(), null);
        if(state==null){
            throw new NullPointerException("Business intent resulted in no application state");
        }
        if(state.getStakeHolder()==null){
            throw new NullPointerException("No one owns this application");
        }
        // TODO a trigger for application state creation handles launching the worker or broadcasting worker?


        //launch worker
        context.getRuntimeContext().getServiceBus().fireEvent(worker, context.getRuntimeContext(), null);

        return CONTINUE_PROCESSING;
    }
}
