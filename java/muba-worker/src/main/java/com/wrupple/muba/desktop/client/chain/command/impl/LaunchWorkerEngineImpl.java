package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import java.util.Arrays;


public class LaunchWorkerEngineImpl extends ChainBase implements LaunchWorkerEngine {

    @Inject
    public LaunchWorkerEngineImpl(ReadWorkerMetadata read, StartWorkerHeartBeat presence, LaunchApplicationState launch) {
        super(Arrays.asList(read, presence, launch));
    }
}
