package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by japi on 11/05/17.
 */
@Singleton
public class ApplicationContextImpl extends ContextBase implements ApplicationContext {
    private final ProcessManager processManager;
    private RuntimeContext runtimeContext;
    private final Solver solver;

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    @Override
    public Solver getSolver() {
        return solver;
    }

    private ApplicationState stateValue;


    @Inject
    public ApplicationContextImpl(ProcessManager processManager, Solver solver) {
        this.processManager = processManager;
        this.solver = solver;
    }

    @Override
    public ProcessManager getProcessManager() {
        return processManager;
    }

    @Override
    public ApplicationContext setRuntimeContext(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
        return this;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }
}
