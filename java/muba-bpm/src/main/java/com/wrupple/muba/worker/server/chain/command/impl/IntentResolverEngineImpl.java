package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.Invocation;
import com.wrupple.muba.worker.domain.IntentResolverContext;
import com.wrupple.muba.worker.server.chain.IntentResolverEngine;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 29/07/17.
 */
public class IntentResolverEngineImpl implements IntentResolverEngine {


    @Override
    public boolean execute(IntentResolverContext context) throws Exception {
        Contract contract = (Contract) context.getRuntimeContext().getServiceContract();
        Invocation resolver = context.getRuntimeContext().getServiceBus().getIntentInterpret().resolveIntent(contract, context.getRuntimeContext());
        resolver.setDomain(contract.getDomain());
        context.getRuntimeContext().setResult(resolver);

        return CONTINUE_PROCESSING;
    }
}
