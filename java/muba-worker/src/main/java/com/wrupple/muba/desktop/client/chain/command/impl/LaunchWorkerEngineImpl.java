package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.CreateWorkerStructure;
import com.wrupple.muba.desktop.client.chain.command.SwitchWorkerContext;
import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import java.util.Arrays;


public class LaunchWorkerEngineImpl extends ChainBase<ContainerContext> implements LaunchWorkerEngine {

    @Inject
    public LaunchWorkerEngineImpl(ReadWorkerMetadata read, CreateWorkerStructure create, StartWorkerHeartBeat presence, SwitchWorkerContext launch) {
        super(Arrays.asList(read, create, presence, launch));
    }
}
