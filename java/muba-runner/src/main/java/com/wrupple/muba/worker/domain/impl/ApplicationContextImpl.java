package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.ApplicationContext;
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

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    @Override
    public void setName(String command) {
        this.name=command;
    }


    private ApplicationState stateValue;

    @Override
    public void setRuntimeContext(RuntimeContext requestContext) {
        Object contract = requestContext.getServiceContract();
        ApplicationState state;
        if(contract instanceof ApplicationState){
           state = (ApplicationState) contract;

        }else{
            Intent intent = (Intent) contract;
            state = intent.getStateValue();
        }
        WorkerState container = state.getWorkerStateValue();
        if (container == null) {
            throw new IllegalStateException("No application container");
        }
        setStateValue(state);
        this.runtimeContext=requestContext;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }


    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public String getName() {
        return name;
    }
}
