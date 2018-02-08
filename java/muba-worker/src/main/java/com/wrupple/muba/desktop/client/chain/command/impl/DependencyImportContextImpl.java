package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.desktop.domain.DependencyImportContext;
import com.wrupple.muba.event.domain.ApplicationDependency;
import com.wrupple.muba.worker.server.service.StateTransition;
import org.apache.commons.chain.impl.ContextBase;

import java.util.Map;

public class DependencyImportContextImpl extends ContextBase implements DependencyImportContext {

    private final ApplicationDependency dependency;
    private final ContextSwitchRuntimeContext applicationSwitchContext;
    private final StateTransition<DependencyImportContext> callback;


    public DependencyImportContextImpl(ApplicationDependency dependency, ContextSwitchRuntimeContext applicationSwitchContext,StateTransition<DependencyImportContext> callback) {
        this.dependency = dependency;
        this.applicationSwitchContext = applicationSwitchContext;
        this.callback=callback;
    }

    @Override
    public ContextSwitchRuntimeContext getApplicationSwitchContext() {
        return applicationSwitchContext;
    }

    @Override
    public ApplicationDependency getDependency() {
        return dependency;
    }

    @Override
    public StateTransition<DependencyImportContext> getCallback() {
        return callback;
    }

    @Override
    public String getDiscriminator() {
        return dependency.getDiscriminator();
    }

    @Override
    public void setDiscrimniator(String discrimniator) {

    }
}
