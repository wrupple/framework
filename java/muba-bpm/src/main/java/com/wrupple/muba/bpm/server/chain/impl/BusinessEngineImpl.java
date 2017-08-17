package com.wrupple.muba.bpm.server.chain.impl;

import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.command.MaterializeApplicationContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Singleton;

/**
 * TransactionalActivityAssemblyImpl
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessEngineImpl extends ChainBase implements BusinessEngine {


    /*recibo BusinessContextImpl necesito sacar un ApplicationState*/
    @Singleton
    public BusinessEngineImpl(MaterializeApplicationContext materialize) {
        super(materialize);
    }
}
