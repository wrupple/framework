package com.wrupple.muba.bootstrap.domain;

import org.apache.commons.chain.Context;

/**
 * Created by japi on 17/07/17.
 */
public interface ServiceContext extends Context {



    public RuntimeContext getRuntimeContext();

    //void setCallback(/*chain so anyone can add his own callback*/Chain callback);
}
