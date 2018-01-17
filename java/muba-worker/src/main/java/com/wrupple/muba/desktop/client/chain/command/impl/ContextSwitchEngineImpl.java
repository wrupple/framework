package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.desktop.client.chain.ContextSwitchEngine;
import com.wrupple.muba.desktop.client.chain.command.ImportResources;
import org.apache.commons.chain.impl.ChainBase;

import java.util.Arrays;

public class ContextSwitchEngineImpl extends ChainBase implements ContextSwitchEngine {

    public ContextSwitchEngineImpl(ImportResources include) {
        super(Arrays.asList(include));
    }

}
