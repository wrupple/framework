package com.wrupple.muba.desktop.client.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.chain.command.PopulateLoadOrder;
import com.wrupple.muba.desktop.client.chain.command.WorkerRequestInterpret;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Provider;

public class WorkerRequestInterpretImpl extends ChainBase implements WorkerRequestInterpret {


    private final Provider<WorkerRequestContext> contextProvider;


    @Inject
    public WorkerRequestInterpretImpl(PopulateLoadOrder pouplate, Provider<WorkerRequestContext> contextProvider) {
        super(new Command[]{pouplate});
        this.contextProvider = contextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) throws Exception {
        return contextProvider.get().setRuntimeContext(requestContext);
    }


}