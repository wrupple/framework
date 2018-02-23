package com.wrupple.muba.event.domain;

import java.util.List;

public interface RemoteServiceContext extends ServiceContext{

    RemoteBroadcast getRequest();

    List<ChannelAgreement> getChannels();

    void setChannels(List<ChannelAgreement> channels);
}
