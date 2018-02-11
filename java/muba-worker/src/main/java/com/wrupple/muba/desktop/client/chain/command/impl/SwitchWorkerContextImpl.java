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

        // this context is universally available through LaunchWorkerInterpret.getWorker()


        // Goes to the place represented on URL else default place
        log.info("Desktop Loading finished, handling current application state");
/*
    //hide home toolbar when in home activity
            eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeListener(main, toolbar, dm));
        eventBus.addHandler(DesktopProcessEvent.TYPE, toolbar);
        eventBus.addHandler(VegetateEvent.TYPE, toolbar);




     */

        ContextSwitch order = context.getContextSwitch();
        order.setDomain(order.getWorkerStateValue().getDomain());
        context.getRuntimeContext().getEventBus().fireEvent(order, context.getRuntimeContext(), null);




        return CONTINUE_PROCESSING;
    }

}
