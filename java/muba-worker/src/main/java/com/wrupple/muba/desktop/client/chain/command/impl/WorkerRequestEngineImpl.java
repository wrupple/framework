package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.WorkerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.*;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerRequestEngineImpl extends ChainBase implements WorkerRequestEngine {

    @Inject
    public WorkerRequestEngineImpl(BuildApplicationTree sliceWriter, DeclareDependencies imports, BindApplication setInitialActivity, BindHost setHost, DesktopWriterCommand write) {
        super(new WorkerRequestEngine.Handler[]{sliceWriter, imports,setInitialActivity,setHost,write});
    }
}
