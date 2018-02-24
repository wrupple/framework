package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.vegetate.chain.command.RemoteServiceInterpret;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class RemoteServiceInterpretImpl implements RemoteServiceInterpret {
    private final Provider<RemoteServiceContext> provider;

    @Inject
    public RemoteServiceInterpretImpl(Provider<RemoteServiceContext> provider) {
        this.provider = provider;
    }

    @Override
    public Provider<RemoteServiceContext> getProvider(RuntimeContext runtime) {
        return provider;
    }

    @Override
    public boolean execute(RuntimeContext context) throws Exception {
        return CONTINUE_PROCESSING;
    }
}
