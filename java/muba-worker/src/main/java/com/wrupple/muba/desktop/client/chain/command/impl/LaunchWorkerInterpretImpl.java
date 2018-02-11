package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchWorkerInterpret;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.desktop.domain.impl.ContainerContextImpl;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
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
        WorkerState request = (WorkerState) parent.getServiceContract();
        if (request == null) {
            throw new IllegalStateException("No container definition!");
        }
        parent.setServiceContract(request);

        ContextSwitch contextSwitch = contractProvider.get();
        contextSwitch.setWorkerStateValue(request);
        return new ContainerContextImpl(parent, contextSwitch, request, pm);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        RuntimeContext requestContext = (RuntimeContext) ctx;
        WorkerState request = (WorkerState) requestContext.getServiceContract();
        ContainerContext context = requestContext.getServiceContext();

        /*
         *Setup Container
         */

        return CONTINUE_PROCESSING;
    }


}
