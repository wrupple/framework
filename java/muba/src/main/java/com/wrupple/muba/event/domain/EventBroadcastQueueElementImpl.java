package com.wrupple.muba.event.domain;

import java.util.List;

public class EventBroadcastQueueElementImpl extends CatalogEntryImpl implements EventBroadcastQueueElement {
    private  Intent eventValue;
    private List<FilterCriteria> observersValues;

    @Override
    public Intent getEventValue() {
        return eventValue;
    }

    @Override
    public void setEventValue(Intent eventValue) {
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
        return EventBroadcastQueueElement.CATALOG;
    }

    @Override
    public Object getCatalog() {
        return eventValue==null? null : eventValue.getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }

}
