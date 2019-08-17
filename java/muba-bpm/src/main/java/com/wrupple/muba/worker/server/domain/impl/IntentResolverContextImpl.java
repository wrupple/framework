package com.wrupple.muba.worker.server.domain.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.IntentResolverContext;
import org.apache.commons.chain.impl.ContextBase;

/**
 * Created by japi on 12/08/17.
 */
public class IntentResolverContextImpl extends ContextBase implements IntentResolverContext {


    private RuntimeContext runtimeContext;


    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
    }
}
