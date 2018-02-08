package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.impl.ContextBase;

public class ContextSwitchRuntimeContextImpl extends ContextBase implements ContextSwitchRuntimeContext {
    private RuntimeContext runtimeContext;

    @Override
    public ContextSwitch getContextSwitch() {
        return (ContextSwitch) runtimeContext.getServiceContract();
    }

    @Override
    public ContextSwitchRuntimeContext intialize(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
        return this;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
}
