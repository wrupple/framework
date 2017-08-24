package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;

/**
 * Created by japi on 16/08/17.
 */
public interface BusinessContext extends ServiceContext {
    BusinessContext setRuntimeContext(RuntimeContext requestContext);

    boolean isChanged();
}
