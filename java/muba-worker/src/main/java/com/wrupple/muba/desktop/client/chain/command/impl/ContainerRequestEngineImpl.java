package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.ContainerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.BuildApplicationTree;
import com.wrupple.muba.desktop.client.chain.command.DeclareDependencies;
import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContainerRequestEngineImpl extends ChainBase implements ContainerRequestEngine {

    @Inject
    public ContainerRequestEngineImpl(BuildApplicationTree sliceWriter, DeclareDependencies imports,DesktopWriterCommand write) {
        super(new ContainerRequestEngine.Handler[]{sliceWriter, imports,write});
    }
}
