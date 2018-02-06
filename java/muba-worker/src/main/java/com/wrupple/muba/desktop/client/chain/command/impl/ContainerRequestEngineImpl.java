package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.ContainerRequestEngine;
import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ContainerRequestEngineImpl extends ChainBase implements ContainerRequestEngine {

    @Inject
    public ContainerRequestEngineImpl(DesktopWriterCommand write) {
        super(new Command[]{write});
    }
}
