package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventDispatcherImpl extends ChainBase<RuntimeContext> implements EventDispatcher {


    @Inject
    public EventDispatcherImpl(ValidateContext validateContext, BindService bind, Incorporate incorporate, ValidateContract validateContract, Run run) {
        super(new Command[]{validateContext, bind, incorporate, validateContract, run});

	}

    @Override
    public boolean execute(RuntimeContext context) throws Exception {
        if(log.isTraceEnabled()){
            StringBuilder builder = new StringBuilder(500);
            Object contract = context.getServiceContract();
            RuntimeContext parent = context.getParentValue();
            while(parent!=null){
                builder.append('\t');
                parent = parent.getParentValue();
            }
            builder.append(contract);
            log.trace(builder.toString());
        }
        return super.execute(context);
    }
}
