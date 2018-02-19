package com.wrupple.muba.event.domain;

import org.apache.commons.chain.Context;

/**
 * Created by japi on 17/07/17.
 */
public interface ServiceContext extends Context {

    public RuntimeContext getRuntimeContext();
    void setRuntimeContext(RuntimeContext context);

    //void setCallback(/*chain so anyone can add his own callback*/Chain callback);
}
