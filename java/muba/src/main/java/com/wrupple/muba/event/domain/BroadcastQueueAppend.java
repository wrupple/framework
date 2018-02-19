package com.wrupple.muba.event.domain;

public interface BroadcastQueueAppend extends Contract {
    String CATALOG = "BroadcastQueueAppend";

    void setHostValue(Host host);

    void setQueuedElementValue(BroadcastEvent queueElement);
}
