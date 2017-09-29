package com.wrupple.muba.event.domain;


import java.util.List;

public interface EventBroadcastQueueElement extends Intent{

    String CATALOG = "EventBroadcastQueueElement";

    Intent getEventValue();

    void setEventValue(Intent event);

    void setObserversValues(List<FilterCriteria> explicitlySuscriptedObservers);
}
