package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.vegetate.chain.command.LocalServiceDelegation;

public class LocalServiceDelegationImpl implements LocalServiceDelegation {
    @Override
    public boolean execute(RemoteServiceContext context) throws Exception {
        BroadcastEvent element = context.getRequest().getQueuedElementValue();
        Contract event = element.getEventValue();
        
        context.getRuntimeContext().getServiceBus().fireEvent(event,context.getRuntimeContext(),null);
        
        return CONTINUE_PROCESSING;
    }
}
