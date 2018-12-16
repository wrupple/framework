package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.FilterCriteria;

import java.util.List;

public class BroadcastEventImpl extends CatalogEntryImpl implements BroadcastEvent {
    private Contract eventValue;
    private CatalogDescriptor catalogDescriptor;
    private List<FilterCriteria> observersValues;

    @Override
    public Contract getEventValue() {
        return eventValue;
    }

    @Override
    public void setEventValue(Contract contractValue) {
        this.eventValue = contractValue;
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

    @Override
    public CatalogDescriptor getCatalogDescriptor() {
        return catalogDescriptor;
    }

    public void setCatalogDescriptor(CatalogDescriptor catalogDescriptor) {
        this.catalogDescriptor = catalogDescriptor;
    }

    @Override
    public String toString() {
        return "BroadcastEventImpl{" +
                "eventValue=" + eventValue +
                ", observers=" + observersValues +
                '}';
    }
}
