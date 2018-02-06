package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.SwitchWorkerContext;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Singleton
public class SwitchWorkerContextImpl implements SwitchWorkerContext {

    protected Logger log = LoggerFactory.getLogger(SwitchWorkerContextImpl.class);

    @Override
    public boolean execute(ContainerContext context ) throws Exception {

        // this context is universally available through WorkerContainerLauncher.getContainer()


        // Goes to the place represented on URL else default place
        log.info("Desktop Loading finished, handling current application state");

        ContextSwitch order = context.handleCurrentApplictionState();

        context.getRuntimeContext().getEventBus().fireEvent(order, context.getRuntimeContext(), null);




        return CONTINUE_PROCESSING;
    }

}
