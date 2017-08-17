package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import org.apache.commons.chain.impl.ContextBase;

/**
 * Created by japi on 16/08/17.
 */
public class BusinessContextImpl extends ContextBase implements BusinessContext {

    RuntimeContext runtimeContext;

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public BusinessContext setRuntimeContext(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
        return this;
    }
}
