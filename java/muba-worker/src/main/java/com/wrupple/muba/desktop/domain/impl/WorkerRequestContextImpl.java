package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.WorkerState;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;

public class WorkerRequestContextImpl extends ContextBase implements WorkerRequestContext {
    private RuntimeContext runtimeContext;
    private WorkerState workerState;

    @Override
    public WorkerState getWorkerState() {
        return workerState;
    }

    @Override
    public WorkerContract getRequest() {
        return (WorkerContract) runtimeContext.getServiceContract();
    }

    @Override
    public void setRuntimeContext(RuntimeContext requestContext) {
        this.runtimeContext = requestContext;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public void setWorkerState(WorkerState workerState) {
        this.workerState = workerState;
    }
}
