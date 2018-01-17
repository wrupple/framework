package com.wrupple.muba.worker.server.chain.impl;

import com.wrupple.muba.worker.server.chain.BusinessEngine;
import com.wrupple.muba.worker.server.chain.command.CommitSubmission;
import com.wrupple.muba.worker.server.chain.command.InferNextTask;
import com.wrupple.muba.worker.server.chain.command.UpdateApplicationContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TransactionalActivityAssemblyImpl
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessEngineImpl extends ChainBase implements BusinessEngine {


    /*recibo ApplicationContextImpl necesito sacar un ApplicationState*/
    @Inject
    public BusinessEngineImpl(CommitSubmission commit, InferNextTask fwd, UpdateApplicationContext update) {
        super(new Command[]{ commit, fwd,update});
    }
}
