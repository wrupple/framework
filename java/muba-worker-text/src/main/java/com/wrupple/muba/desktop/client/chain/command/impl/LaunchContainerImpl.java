package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchContainer;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.desktop.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;

@Singleton
public class LaunchContainerImpl
        implements LaunchContainer {

    private final Provider<LaunchWorker> contractProvider;

    @Inject
    public LaunchContainerImpl(Provider<LaunchWorker> contractProvider) {
        this.contractProvider = contractProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws InvocationTargetException, IllegalAccessException {
        LaunchWorker request = (LaunchWorker) parent.getServiceContract();
        if (request == null) {
            request = contractProvider.get();
            parent.setServiceContract(request);
        }
        return new ContainerContextImpl(parent);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) ctx;
        LaunchWorker request = (LaunchWorker) requestContext.getServiceContract();
        ContainerContext context = requestContext.getServiceContext();


        /*
         * Setup Container
         */


        //TODO createApplication();


        return CONTINUE_PROCESSING;
    }
}
