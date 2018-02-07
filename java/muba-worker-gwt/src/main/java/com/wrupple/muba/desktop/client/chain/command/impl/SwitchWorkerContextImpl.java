package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.SwitchWorkerContext;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SwitchWorkerContextImpl implements SwitchWorkerContext {


    @Override
    public boolean execute(ContainerContext context) throws Exception {



        context.getRuntimeContext().getEventBus().fireEvent(context.getContextSwitch(), context.getRuntimeContext(), null);


        return CONTINUE_PROCESSING;
    }
}
