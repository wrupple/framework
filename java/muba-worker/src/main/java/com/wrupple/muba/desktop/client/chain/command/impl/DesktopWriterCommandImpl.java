package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.WorkerState;

public class DesktopWriterCommandImpl implements DesktopWriterCommand {

    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {

        /*TODO vegetate channel fire load order, in this case the handler will live in the same container.

        but in gwt, web, and others the handler of the order will live in a remote instance that may not be created yet
        vegetate receives a runtime context and prints a ${tagged} html formated document

        */
        WorkerState worker = context.getWorkerState();
        worker.setRunner(context.getRequest().getRunner());

        context.getRuntimeContext().getEventBus().fireEvent(worker, context.getRuntimeContext(), null);

        return CONTINUE_PROCESSING;
    }
}
