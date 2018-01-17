package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.worker.domain.WorkerLoadOrder;
import org.apache.commons.chain.Context;

public interface DesktopRequestContext extends ServiceContext {
    WorkerLoadOrder getWorkerOrderValue();

    Context setRuntimeContext(RuntimeContext requestContext);
}
