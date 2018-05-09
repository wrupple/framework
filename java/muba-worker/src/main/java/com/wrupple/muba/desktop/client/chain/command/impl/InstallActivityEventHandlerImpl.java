package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.domain.Trigger;
import com.wrupple.muba.catalogs.domain.TriggerImpl;
import com.wrupple.muba.desktop.client.chain.command.ApplicationStateListener;
import com.wrupple.muba.desktop.client.chain.command.InstallActivityEventHandler;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.event.server.service.ActionsDictionary;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InstallActivityEventHandlerImpl implements InstallActivityEventHandler {


    //FIXME private final ActivityVegetateEventHandler vegetateHandler;

    @Inject
    public InstallActivityEventHandlerImpl(ApplicationStateListener listener, ActionsDictionary actions){
        //FIXME do it in a plugin
        actions.addCommand(ApplicationStateListener.class.getSimpleName(),listener);
    }

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

        TriggerImpl trigger = new TriggerImpl(-21l,1, ApplicationStateListener.class.getSimpleName(), false,
                ApplicationState.CATALOG, null, null);
        trigger.setFailSilence(false);
        trigger.setStopOnFail(true);
        return trigger;
    }
}
