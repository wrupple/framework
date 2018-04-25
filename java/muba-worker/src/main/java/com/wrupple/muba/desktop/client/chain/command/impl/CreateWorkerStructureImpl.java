package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.CreateWorkerStructure;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.Application;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Provider;

public class CreateWorkerStructureImpl implements CreateWorkerStructure {
    /*
     NestedActivityPresenter main = new NestedActivityPresenter(dm);
            main.setStyleName("desktop");
     */
    private final Provider<ProcessWindow> presenterProvider;
    protected Logger log = LogManager.getLogger(CreateWorkerStructureImpl.class);

    @Inject
    public CreateWorkerStructureImpl(Provider<ProcessWindow> presenterProvider) {
        this.presenterProvider = presenterProvider;
    }

    @Override
    public boolean execute(ContainerContext context) throws Exception {



        //Application root = context.getHomeApplicationValue();
        //Application applicationItem = context.getApplicationItem();

        log.info("installing processWindow ");
        context.setDisplay(presenterProvider.get());



        return CONTINUE_PROCESSING;
    }
}
