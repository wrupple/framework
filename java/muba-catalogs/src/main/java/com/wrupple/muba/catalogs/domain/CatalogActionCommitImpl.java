package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntryImpl;

public class CatalogActionCommitImpl extends CatalogEntryImpl implements CatalogActionCommit{
    private CatalogActionRequest requestValue;
    private CatalogActionContext liveContext;

    @Override
    public CatalogActionRequest getRequestValue() {
        return requestValue;
    }

    @Override
    public void setRequestValue(CatalogActionRequest requestValue) {
        this.requestValue = requestValue;
    }

    @Override
    public CatalogActionContext getLiveContext() {
        return liveContext;
    }

    @Override
    public void setLiveContext(CatalogActionContext liveContext) {
        this.liveContext = liveContext;
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
}
