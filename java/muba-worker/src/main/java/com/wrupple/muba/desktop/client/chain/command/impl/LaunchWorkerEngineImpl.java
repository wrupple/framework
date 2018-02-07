package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.*;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import java.util.Arrays;


public class LaunchWorkerEngineImpl extends ChainBase<ContainerContext> implements LaunchWorkerEngine {

    @Inject
    public LaunchWorkerEngineImpl(ReadWorkerMetadata read, CreateWorkerStructure create, StartWorkerHeartBeat presence, InstallActivityEventHandler activityVegetateEventHandler, SwitchWorkerContext launch) {
        super(Arrays.asList(read, create, presence,activityVegetateEventHandler, launch));
    }
}
