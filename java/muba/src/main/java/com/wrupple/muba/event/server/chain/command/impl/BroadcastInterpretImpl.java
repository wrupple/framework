package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.EventBroadcastQueueElement;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by japi on 30/09/17.
 */
public class BroadcastInterpretImpl implements BroadcastInterpret {

    private final Provider<BroadcastContext> contextProvider;

    @Inject
    public BroadcastInterpretImpl(Provider<BroadcastContext> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        return contextProvider.get();
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext requestContext = (RuntimeContext) ctx;
        EventBroadcastQueueElement contract = (EventBroadcastQueueElement) requestContext.getServiceContract();
        BroadcastContext context = requestContext.getServiceContext();
        context.setElement(contract);

        return Command.CONTINUE_PROCESSING;
    }
}
