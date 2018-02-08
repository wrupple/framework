package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.ContextSwitchInterpret;
import com.wrupple.muba.desktop.domain.ContextSwitchRuntimeContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;

public class ContextSwitchInterpretImpl implements ContextSwitchInterpret {

    private Provider<ContextSwitchRuntimeContext> contextProvider;

    @Inject
    public ContextSwitchInterpretImpl(Provider<ContextSwitchRuntimeContext> contextProvider) {
        this.contextProvider = contextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) throws Exception {
        return contextProvider.get().intialize(requestContext);
    }

    @Override
    public boolean execute(Context context) throws Exception {
        return CONTINUE_PROCESSING;
    }
}
