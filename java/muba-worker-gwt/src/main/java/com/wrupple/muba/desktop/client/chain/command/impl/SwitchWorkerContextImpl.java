package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.SwitchWorkerContext;
import com.wrupple.muba.desktop.domain.ContainerContext;

import javax.inject.Singleton;

@Singleton
public class SwitchWorkerContextImpl implements SwitchWorkerContext {


    @Override
    public boolean execute(ContainerContext context) throws Exception {



        context.getRuntimeContext().getEventBus().fireEvent(context.getContextSwitch(), context.getRuntimeContext(), null);


        return CONTINUE_PROCESSING;
    }
}
