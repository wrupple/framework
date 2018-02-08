package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.WorkerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.BuildApplicationTree;
import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.client.chain.command.BindApplication;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkerRequestEngineImpl extends ChainBase implements WorkerRequestEngine {

    @Inject
    public WorkerRequestEngineImpl(BuildApplicationTree sliceWriter, DeclareDependencies imports, BindApplication setInitialActivity, DesktopWriterCommand write) {
        super(new WorkerRequestEngine.Handler[]{sliceWriter, imports,setInitialActivity,write});
    }
}
