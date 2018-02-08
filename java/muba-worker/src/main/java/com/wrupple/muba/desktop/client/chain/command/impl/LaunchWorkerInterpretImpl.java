package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchWorkerInterpret;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.desktop.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ApplicationState;
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
public class LaunchWorkerInterpretImpl implements LaunchWorkerInterpret {

    private final Provider<ContextSwitch> contractProvider;
    private final ProcessManager pm;

    @Inject
    public LaunchWorkerInterpretImpl(Provider<ContextSwitch> contractProvider, ProcessManager pm) {
        this.contractProvider = contractProvider;
        this.pm = pm;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext parent) throws Exception {
        ContainerState request = (ContainerState) parent.getServiceContract();
        if (request == null) {
            throw new IllegalStateException("No container definition!");
        }
        pm.setContainer(request, parent);
        parent.setServiceContract(request);

        ContextSwitch contextSwitch = contractProvider.get();
        contextSwitch.setOrderValue(request);
        return new ContainerContextImpl(parent, contextSwitch, request, pm);
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
