package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.BindHost;
import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.WorkerState;

import java.util.List;

public class BindHostImpl implements BindHost {
    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {


        WorkerContract request = context.getRequest();

        WorkerState container = context.getWorkerState();

        container.setHostValue(request.getHostValue());
        container.setHost((Long) request.getHostValue().getId());

        return CONTINUE_PROCESSING;
    }
}
