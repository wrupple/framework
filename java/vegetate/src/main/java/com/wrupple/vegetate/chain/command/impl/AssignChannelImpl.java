package com.wrupple.vegetate.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogQueryRequestImpl;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.vegetate.chain.command.AssignChannel;
import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.RemoteServiceContext;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AssignChannelImpl implements AssignChannel {

    @Override
    public boolean execute(RemoteServiceContext context) throws Exception {
        //Host threadHost = context.getRuntimeContext().getSession().getSessionValue().getPeerValue();
        RemoteBroadcast broadcast = context.getRequest();
        Host runningHost = broadcast.getHostValue();
        FilterData filter= FilterDataUtils.createSingleFieldFilter(CatalogEntry.ID_FIELD,runningHost.getId());
        CatalogQueryRequestImpl inquiry = new CatalogQueryRequestImpl(filter, ChannelAgreement.CATALOG);

        List<ChannelAgreement> channels= context.getRuntimeContext().getServiceBus().fireEvent(inquiry,context.getRuntimeContext(),null);

        if(channels==null|| channels.isEmpty()){
            throw new IllegalStateException("No adecuate channel found for broadcast");
        }
        context.setChannels(channels);


        return CONTINUE_PROCESSING;
    }
}
