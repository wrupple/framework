package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasHostValue;

public interface RemoteBroadcast extends Contract,HasHostValue {
    String CATALOG = "RemoteBroadcast";

    void setQueuedElementValue(BroadcastEvent queueElement);

    BroadcastEvent getQueuedElementValue();

    void setHostValue(Host host);
}
