package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.IntentResolverContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.bpm.server.chain.command.IntentResolverRequestInterpret;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Created by japi on 29/07/17.
 */
@Singleton
public class IntentResolverRequestInterpretImpl implements IntentResolverRequestInterpret {

    private final Provider<IntentResolverContext> contextProvider;

    @Inject
    public IntentResolverRequestInterpretImpl(Provider<IntentResolverContext> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        IntentResolverContext r = contextProvider.get();
        r.setExcecutionContext(requestContext);
        return r;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
       /* RuntimeContext requestContext = (RuntimeContext) ctx;
        IntentResolverContext context = requestContext.getServiceContext();
        ImplicitIntent request = (ImplicitIntent) requestContext.getServiceContract();
        if(request==null){
                throw new NullPointerException("there is no intent definition");
        }*/

        return CONTINUE_PROCESSING;
    }
}