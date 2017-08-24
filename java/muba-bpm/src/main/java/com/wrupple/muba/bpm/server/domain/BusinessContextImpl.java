package com.wrupple.muba.bpm.server.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.impl.ContextBase;

/**
 * Created by japi on 16/08/17.
 */
public class BusinessContextImpl extends ContextBase implements BusinessContext {

    RuntimeContext runtimeContext;
    private boolean changed;

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public BusinessContext setRuntimeContext(RuntimeContext requestContext) {
        this.runtimeContext=requestContext;
        return this;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
