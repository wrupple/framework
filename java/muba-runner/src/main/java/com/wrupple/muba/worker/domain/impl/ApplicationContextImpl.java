package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Singleton;

/**
 * Created by japi on 11/05/17.
 */
@Singleton
public class ApplicationContextImpl extends ContextBase implements ApplicationContext {
    private RuntimeContext runtimeContext;
    private String name;
    private WorkerState workerStateValue;

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    @Override
    public void setName(String command) {
        this.name=command;
    }

    @Override
    public Command getStateInstance(Task step) {
        return null;
    }


    private ApplicationState stateValue;

    @Override
    public ApplicationContext setRuntimeContext(RuntimeContext requestContext, WorkerState container) {
        this.runtimeContext=requestContext;
        this.workerStateValue = container;
        return this;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }

    @Override
    public WorkerState getWorkerStateValue() {
        return workerStateValue;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public String getName() {
        return name;
    }
}
