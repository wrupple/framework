package com.wrupple.muba.event.domain;

public interface RemoteBroadcast extends Contract {
    String CATALOG = "RemoteBroadcast";

    void setHostValue(Host host);

    void setQueuedElementValue(BroadcastEvent queueElement);
}
