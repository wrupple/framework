package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.client.chain.command.WorkerRequestInterpret;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.server.chain.command.Run;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Provider;

public class WorkerRequestInterpretImpl implements WorkerRequestInterpret {


    private final Provider<WorkerRequestContext> contextProvider;
    private final Provider<WorkerState> workerProvider;
    private final  PopulateLoadOrder pouplate;


    @Inject
    public WorkerRequestInterpretImpl(PopulateLoadOrder pouplate, Provider<WorkerRequestContext> contextProvider, Provider<WorkerState> workerProvider) {
        this.pouplate=pouplate;
        this.contextProvider = contextProvider;
        this.workerProvider = workerProvider;
    }


    @Override
    public Provider<WorkerRequestContext> getProvider(RuntimeContext runtime) {
        return contextProvider;
    }

    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {
        WorkerRequestContext c = requestContext.getServiceContext();
        //FIXME attemptToReuseExistingChannel
        WorkerState worker = workerProvider.get();
        worker.setDomain(requestContext.getSession().getSessionValue().getDomain());
        c.setWorkerState(worker);

        return pouplate.execute(requestContext);
    }
}
