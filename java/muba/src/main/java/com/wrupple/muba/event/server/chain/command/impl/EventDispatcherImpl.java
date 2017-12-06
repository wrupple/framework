package com.wrupple.muba.event.server.chain.command.impl;

import com.wrupple.muba.event.server.chain.command.*;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventDispatcherImpl extends ChainBase implements EventDispatcher {


    @Inject
    public EventDispatcherImpl(ValidateRequest validateRequest, BindService bind, Incorporate incorporate, ValidateContract validateContract, Dispatch dispatch) {
        super(new Command[]{validateRequest, bind, incorporate, validateContract, dispatch});

	}


}
