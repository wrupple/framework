package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.command.LaunchWorkerInterpret;
import com.wrupple.muba.desktop.domain.ContainerContext;
import com.wrupple.muba.event.domain.RuntimeContext;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 *
 * singleton ensures one container per factory/injector
 */
@Singleton
public class LaunchWorkerInterpretImpl implements LaunchWorkerInterpret {


    private final Provider<ContainerContext> provider;

    @Inject
    public LaunchWorkerInterpretImpl(Provider<ContainerContext> provider) {
        this.provider = provider;
    }


    @Override
    public Provider<ContainerContext> getProvider(RuntimeContext runtime) {
        return provider;
    }


    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {
        return CONTINUE_PROCESSING;
    }

}
