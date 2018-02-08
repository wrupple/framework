package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.domain.impl.AbstractYieldContext;
import com.wrupple.muba.worker.server.service.ProcessManager;

import javax.inject.Inject;

public class ContainerContextImpl extends AbstractYieldContext implements ContainerContext {
    private final RuntimeContext runtimeContext;
    private final ContextSwitch contextSwitch;
    private final ProcessManager processManager;

    @Inject
    public ContainerContextImpl(RuntimeContext runtimeContext, ContextSwitch applicationState, WorkerState state, ProcessManager processManager) {
        this.runtimeContext = runtimeContext;
        this.contextSwitch=applicationState;
        this.processManager = processManager;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
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

    @Override
    public ContextSwitch handleCurrentApplictionState() {
        return null;
    }
}
