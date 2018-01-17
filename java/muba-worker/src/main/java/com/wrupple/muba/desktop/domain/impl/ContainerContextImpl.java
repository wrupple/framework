package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;

public class ContainerContextImpl extends ContextBase implements ContainerContext {
    private final RuntimeContext runtimeContext;
    private final ApplicationState state;

    @Inject
    public ContainerContextImpl(RuntimeContext runtimeContext, ApplicationState applicationState) {
        this.runtimeContext = runtimeContext;
        this.state=applicationState;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }


    @Override
    public ApplicationState getState() {
        return state;
    }
}
