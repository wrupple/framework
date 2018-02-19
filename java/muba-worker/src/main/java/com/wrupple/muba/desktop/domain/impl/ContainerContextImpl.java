package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.domain.impl.AbstractYieldContext;
import com.wrupple.muba.worker.server.service.ProcessManager;

import javax.inject.Inject;
import javax.inject.Provider;


public class ContainerContextImpl extends AbstractYieldContext implements ContainerContext {
    private  RuntimeContext runtimeContext;
    private final ContextSwitch contextSwitch;
    private final ProcessManager processManager;




    @Inject
    public ContainerContextImpl(ProcessManager processManager,Provider<ContextSwitch> contractProvider) {
        this.processManager = processManager;
         contextSwitch = contractProvider.get();

    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext parent) {
        WorkerState request = (WorkerState) parent.getServiceContract();
        if (request == null) {
            throw new IllegalStateException("No container definition!");
        }
        parent.setServiceContract(request);
        contextSwitch.setWorkerStateValue(request);

        this.runtimeContext=parent;
    }


    @Override
    public ContextSwitch getContextSwitch() {
        return contextSwitch;
    }

    @Override
    public ProcessManager getProcessManager() {
        return processManager;
    }

    @Override
    public void setDisplay(ProcessWindow processWindow) {

    }

}
