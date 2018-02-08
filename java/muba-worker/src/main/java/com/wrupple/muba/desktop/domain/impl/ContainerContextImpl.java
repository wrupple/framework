package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;

public class ContainerContextImpl extends ContextBase implements ContainerContext {
    private final RuntimeContext runtimeContext;
    private final ContextSwitch contextSwitch;
    private final ProcessManager processManager;

    @Inject
    public ContainerContextImpl(RuntimeContext runtimeContext, ContextSwitch applicationState, ContainerState state, ProcessManager processManager) {
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
