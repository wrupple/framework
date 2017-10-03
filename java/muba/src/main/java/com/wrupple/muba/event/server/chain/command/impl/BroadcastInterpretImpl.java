package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.BroadcastContext;
import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by japi on 30/09/17.
 */
public class BroadcastInterpretImpl implements BroadcastInterpret {

    private final Provider<BroadcastContext> contextProvider;
    private final EventSuscriptionMapper concernedInterests;

    @Inject
    public BroadcastInterpretImpl(Provider<BroadcastContext> contextProvider,EventSuscriptionMapper concernedInterests) {
        this.contextProvider = contextProvider;
        this.concernedInterests=concernedInterests;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        return contextProvider.get().setRuntimeContext(requestContext);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext requestContext = (RuntimeContext) ctx;
        BroadcastEvent contract = (BroadcastEvent) requestContext.getServiceContract();
        BroadcastContext context = requestContext.getServiceContext();
        context.setEventValue(contract);
        return concernedInterests.execute(context);
    }
}
