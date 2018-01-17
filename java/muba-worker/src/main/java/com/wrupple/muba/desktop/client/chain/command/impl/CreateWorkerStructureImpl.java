package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.CreateWorkerStructure;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.worker.domain.Application;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

public class DeskCreateWorkerStructureImpl implements CreateWorkerStructure {
    /*
     NestedActivityPresenter main = new NestedActivityPresenter(dm);
            main.setStyleName("desktop");
     */
    private final Provider<ProcessWindow> presenterProvider;
    protected Logger log = LoggerFactory.getLogger(CreateWorkerStructureImpl.class);

    @Inject
    public CreateWorkerStructureImpl(Provider<ProcessWindow> presenterProvider) {
        this.presenterProvider = presenterProvider;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        ContainerContext context = (ContainerContext) ctx;
        Application root = context.getHomeApplicationValue();


        Application applicationItem = context.getApplicationItem();

        context.setDisplay(presenterProvider.get());


        log.info("Desktop Loading finished, handling current application state");

        // Goes to the place represented on URL else default place
        context.handleCurrentApplictionState();


        return CONTINUE_PROCESSING;
    }
}
