package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.event.domain.ImplicitIntent;
import com.wrupple.muba.bpm.domain.IntentResolverContext;
import com.wrupple.muba.bpm.server.chain.IntentResolverEngine;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 29/07/17.
 */
public class IntentResolverEngineImpl implements IntentResolverEngine {


    @Override
    public boolean execute(Context ctx) throws Exception {
        IntentResolverContext context = (IntentResolverContext) ctx;
        ImplicitIntent contract = (ImplicitIntent) context.getRuntimeContext().getServiceContract();

        context.getRuntimeContext().setResult(context.getRuntimeContext().getEventBus().getIntentInterpret().resolveIntent(contract,context.getRuntimeContext()));


        return CONTINUE_PROCESSING;
    }
}
