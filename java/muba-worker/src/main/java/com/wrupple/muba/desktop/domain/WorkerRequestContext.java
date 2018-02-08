package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

public interface WorkerRequestContext extends ServiceContext {
    WorkerState getWorkerState();

    WorkerRequest getRequest();

    Context setRuntimeContext(RuntimeContext requestContext);
}
