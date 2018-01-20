package com.wrupple.muba.desktop.domain;

import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

public interface DesktopRequestContext extends ServiceContext {
    ContainerState getWorkerOrderValue();

    Context setRuntimeContext(RuntimeContext requestContext);
}
