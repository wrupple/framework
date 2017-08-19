package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.ServiceContext;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 16/08/17.
 */
public interface BusinessContext extends ServiceContext {
    BusinessContext setRuntimeContext(RuntimeContext requestContext);

    boolean isChanged();
}
