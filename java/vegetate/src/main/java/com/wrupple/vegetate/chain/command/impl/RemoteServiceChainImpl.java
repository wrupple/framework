package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.muba.event.server.chain.RemoteServiceChain;
import com.wrupple.vegetate.chain.command.AssignChannel;
import com.wrupple.vegetate.chain.command.Send;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import org.apache.commons.chain.impl.ChainBase;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class RemoteServiceChainImpl extends ChainBase<RemoteServiceContext> implements RemoteServiceChain {

    @Inject
    public RemoteServiceChainImpl(AssignChannel assign,Send send){
        super(Arrays.asList(assign,send));
    }
}
