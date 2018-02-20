package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import com.wrupple.muba.desktop.domain.ContainerContext;
import org.apache.commons.chain.Context;

public class StartWorkerHeartBeatImpl implements StartWorkerHeartBeat {
    @Override
    public boolean execute(ContainerContext context) throws Exception {



        return CONTINUE_PROCESSING;
    }
}
