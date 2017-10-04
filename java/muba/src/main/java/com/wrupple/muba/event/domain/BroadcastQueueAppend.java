package com.wrupple.muba.event.domain;

public interface BroadcastQueueAppend extends Event{
    String CATALOG = "BroadcastQueueAppend";

    void setHostValue(Host host);

    void setQueuedElementValue(BroadcastEvent queueElement);
}
