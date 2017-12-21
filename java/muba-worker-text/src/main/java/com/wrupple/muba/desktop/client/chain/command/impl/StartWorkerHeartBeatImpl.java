package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import org.apache.commons.chain.Context;

public class StartWorkerHeartBeatImpl implements StartWorkerHeartBeat {
    @Override
    public boolean execute(Context context) throws Exception {
        return CONTINUE_PROCESSING;
    }
}
