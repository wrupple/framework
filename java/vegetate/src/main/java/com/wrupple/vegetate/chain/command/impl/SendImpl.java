package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.vegetate.chain.command.Send;
import com.wrupple.muba.event.domain.Channel;
import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.RemoteServiceContext;

import java.util.List;

public class SendImpl implements Send {
    @Override
    public boolean execute(RemoteServiceContext context) throws Exception {
        List<ChannelAgreement> channels = context.getChannels();

        Channel service;
        for(ChannelAgreement channel : channels){
            service = channel.getChannelValue();
            service.execute(context);
        }
        
        return CONTINUE_PROCESSING;
    }
}
