package com.wrupple.muba.event.domain;


import java.util.List;

public interface BroadcastEvent extends Contract {

    String CATALOG = "BroadcastEvent";

    Contract getEventValue();

    void setEventValue(Contract contract);

    void setObserversValues(List<FilterCriteria> explicitlySuscriptedObservers);

    List<FilterCriteria> getObserversValues();

    CatalogDescriptor getCatalogDescriptor();

    void setCatalogDescriptor(CatalogDescriptor catalogDescriptor);
}
