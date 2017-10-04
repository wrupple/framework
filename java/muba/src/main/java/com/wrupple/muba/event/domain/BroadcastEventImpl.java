package com.wrupple.muba.event.domain;

import java.util.List;

public class BroadcastEventImpl extends CatalogEntryImpl implements BroadcastEvent {
    private Event eventValue;
    private List<FilterCriteria> observersValues;

    @Override
    public Event getEventValue() {
        return eventValue;
    }

    @Override
    public void setEventValue(Event eventValue) {
        this.eventValue = eventValue;
    }

    public List<FilterCriteria> getObserversValues() {
        return observersValues;
    }

    @Override
    public void setObserversValues(List<FilterCriteria> explicitlySuscriptedObservers) {
        this.observersValues=explicitlySuscriptedObservers;
    }

    @Override
    public String getCatalogType() {
        return BroadcastEvent.CATALOG;
    }

    @Override
    public Object getCatalog() {
        return eventValue==null? null : eventValue.getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }

}
