package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.worker.domain.IntentResolverContext;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 29/07/17.
 */
public class IntentResolverEngineImpl implements IntentResolverEngine {


    @Override
    public boolean execute(Context ctx) throws Exception {
        IntentResolverContext context = (IntentResolverContext) ctx;
        Event contract = (Event) context.getRuntimeContext().getServiceContract();

        context.getRuntimeContext().setResult(context.getRuntimeContext().getEventBus().getIntentInterpret().resolveIntent(contract,context.getRuntimeContext()));


        return CONTINUE_PROCESSING;
    }
}
