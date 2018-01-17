package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.DesktopEngine;
import com.wrupple.muba.desktop.client.chain.command.DesktopWriterCommand;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DesktopEngineImpl extends ChainBase implements DesktopEngine {

    @Inject
    public DesktopEngineImpl(DesktopWriterCommand write) {
        super(new Command[]{write});
    }
}
