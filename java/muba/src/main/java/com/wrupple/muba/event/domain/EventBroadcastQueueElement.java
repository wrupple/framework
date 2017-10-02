package com.wrupple.muba.event.domain;


import java.util.List;

public interface EventBroadcastQueueElement extends Event {

    String CATALOG = "EventBroadcastQueueElement";

    Event getEventValue();

    void setEventValue(Event event);

    void setObserversValues(List<FilterCriteria> explicitlySuscriptedObservers);

    List<FilterCriteria> getObserversValues();
}
