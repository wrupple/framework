package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.ChannelAgreement;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.RemoteServiceContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.impl.ContextBase;

import java.util.List;

public class RemoteServiceContextImpl extends ContextBase implements RemoteServiceContext {

    private List<ChannelAgreement> channels;
    private RuntimeContext runtimeContext;

    @Override
    public RemoteBroadcast getRequest() {
        return (RemoteBroadcast) runtimeContext.getServiceContract();
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public List<ChannelAgreement> getChannels() {
        return channels;
    }

    @Override
    public void setChannels(List<ChannelAgreement> channels) {
        this.channels = channels;
    }
}
