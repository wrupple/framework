package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;

public class ContainerContextImpl extends ContextBase implements ContainerContext {
    private final RuntimeContext runtimeContext;

    @Inject
    public ContainerContextImpl(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
}
