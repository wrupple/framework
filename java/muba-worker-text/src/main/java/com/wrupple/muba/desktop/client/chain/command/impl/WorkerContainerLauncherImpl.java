package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.WorkerContainerLauncher;
import com.wrupple.muba.desktop.client.service.SliceReader;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 *
 * singleton ensures one container per factory/injector
 */
@Singleton
public class WorkerContainerLauncherImpl implements WorkerContainerLauncher {

    private final Provider<ContainerState> contractProvider;
    private final ProcessManager pm;
    private final SliceReader delegate;

    @Inject
    public WorkerContainerLauncherImpl(Provider<ContainerState> contractProvider, ProcessManager pm, SliceReader delegate) {
        this.contractProvider = contractProvider;
        this.pm = pm;
        this.delegate = delegate;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws Exception {
        ContainerState request = (ContainerState) parent.getServiceContract();
        if (request == null) {
            throw new IllegalStateException("No container definition!");
        }
        pm.setContainer(request, parent);
        parent.setServiceContract(request);

        Application activity = delegate.getInitialActivity(request, parent);
        ApplicationState applicationState = null;
        try {
            applicationState = pm.acquireContext(activity, parent);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to acquire application state.", e);
        }
        return new ContainerContextImpl(parent, applicationState, request);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) ctx;
        ContainerState request = (ContainerState) requestContext.getServiceContract();
        ContainerContext context = requestContext.getServiceContext();

        /*
         *Setup Container
         */

        return CONTINUE_PROCESSING;
    }


}
