package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.desktop.client.chain.command.WorkerContainerLauncher;
import com.wrupple.muba.bpm.domain.ContainerContext;
import com.wrupple.muba.desktop.client.service.LoadHumanInterface;
import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.desktop.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 *
 * singleton ensures one container per factory/injector
 */
@Singleton
public class WorkerContainerLauncherImpl
        implements WorkerContainerLauncher {

    private final Provider<LaunchWorker> contractProvider;
    private final ProcessManager pm;
    private final LoadHumanInterface delegate;
    private ContainerContextImpl container;

    @Inject
    public WorkerContainerLauncherImpl(Provider<LaunchWorker> contractProvider, ProcessManager pm, LoadHumanInterface delegate) {
        this.contractProvider = contractProvider;
        this.pm = pm;
        this.delegate = delegate;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws Exception {
        LaunchWorker request = (LaunchWorker) parent.getServiceContract();
        if (request == null) {
            request = contractProvider.get();
            parent.setServiceContract(request);
        }
        if(container==null){
            Application activity=delegate.getInitialActivity(request,parent);
            ApplicationState applicationState = null;
            try {
                applicationState = pm.acquireContext(activity, parent.getSession());
            } catch (Exception e) {
                throw new IllegalStateException("Unable to acquire application state.",e);
            }
            setContainer(new ContainerContextImpl(parent,applicationState));
            //pm.setContainer(getContainer());
            return getContainer();
        }else{
            throw new IllegalStateException("Worker already launched!");
        }
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

    public ContainerContextImpl getContainer() {
        if(container==null){
            throw new IllegalStateException("no worker has been launched!");
        }
        return container;
    }

    public void setContainer(ContainerContextImpl container) {
        this.container = container;
    }
}
