package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import org.apache.commons.chain.Context;

public class CatalogActionFilteringImpl extends CatalogEntryImpl implements CatalogActionFiltering {
    private CatalogActionRequest requestValue;
    private CatalogActionContext stateValue;
    private Object state;

    @Override
    public CatalogActionRequest getRequestValue() {
        return requestValue;
    }

    @Override
    public void setRequestValue(CatalogActionRequest requestValue) {
        this.requestValue = requestValue;
    }

    @Override
    public CatalogActionContext getStateValue() {
        return stateValue;
    }

    @Override
    public void setStateValue(Context liveContext) {
        this.stateValue = (CatalogActionContext) liveContext;
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public Object getCatalog() {
        return getRequestValue().getCatalog();
    }

    @Override
    public void setCatalog(String catalog) {
        getRequestValue().setCatalog(catalog);
    }

    @Override
    public Object getEntry() {
        return getRequestValue().getEntry();
    }

    @Override
    public void setEntry(Object id) {
        getRequestValue().setEntry(id);
    }

    @Override
    public Object getEntryValue() {
        return getRequestValue().getEntryValue();
    }

    @Override
    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }
}
