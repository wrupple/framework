package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.desktop.client.chain.command.InstallActivityEventHandler;
import com.wrupple.muba.desktop.client.chain.command.ApplicationStateListenerImpl;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;

public class InstallActivityEventHandlerImpl implements InstallActivityEventHandler {

    //FIXME private final ActivityVegetateEventHandler vegetateHandler;


    @Override
    public boolean execute(ContainerContext context) throws Exception {
        //callback.hook(vegetateHandler);
        //eventBus.addHandler(VegetateEvent.TYPE, vegetateHandler);

        Trigger applicationUpdate = getStateTrigger();
        CatalogCreateRequestImpl creation = new CatalogCreateRequestImpl(applicationUpdate,Trigger.CATALOG);
        applicationUpdate = context.getRuntimeContext().getServiceBus().fireEvent(creation,context.getRuntimeContext(),null);


        return CONTINUE_PROCESSING;
    }


    private Trigger getStateTrigger() {

        TriggerImpl trigger = new TriggerImpl(1, ApplicationStateListenerImpl.class.getSimpleName(), false,
                ApplicationState.CATALOG, null, null);
        trigger.setFailSilence(false);
        trigger.setStopOnFail(true);
        return trigger;
    }
}
