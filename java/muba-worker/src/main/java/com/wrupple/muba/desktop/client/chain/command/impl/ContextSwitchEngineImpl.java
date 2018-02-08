package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.ContextSwitchEngine;
import com.wrupple.muba.desktop.client.chain.command.ImportResources;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ContextSwitchEngineImpl extends ChainBase implements ContextSwitchEngine {

    @Inject
    public ContextSwitchEngineImpl(ImportResources include) {
        super(Arrays.asList(include));
    }

}
