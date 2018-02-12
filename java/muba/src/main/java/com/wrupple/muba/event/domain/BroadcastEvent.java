package com.wrupple.muba.event.domain;


import java.util.List;

public interface BroadcastEvent extends Event {

    String CATALOG = "BroadcastEvent";

    Event getEventValue();

    void setEventValue(Event event);

    void setObserversValues(List<FilterCriteria> explicitlySuscriptedObservers);

    List<FilterCriteria> getObserversValues();

    CatalogDescriptor getCatalogDescriptor();

    void setCatalogDescriptor(CatalogDescriptor catalogDescriptor);
}
