package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;

/**
 * Created by japi on 12/08/17.
 */
public class BusinessEventImpl extends ManagedObjectImpl implements BusinessEvent {
    @CatalogKey(
            foreignCatalog = ApplicationState.CATALOG
    )
    private Long state;
    @CatalogValue(
            foreignCatalog = ApplicationState.CATALOG
    )
    private ApplicationState stateValue;
    @CatalogValue(
            foreignCatalog = ApplicationItem.CATALOG
    )
    private ApplicationItem handleValue;
    @CatalogKey(
            foreignCatalog = ApplicationItem.CATALOG
    )
    private Long handle;

    //Request Contract
    private CatalogEntry entryValue;

    private Long entry;

    private String catalog;

    @Override
    public Long getState() {
        return state;
    }

    public void setState(Long state) {
        this.state = state;
    }

    public ApplicationState getStateValue() {
        return stateValue;
    }

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    public CatalogEntry getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(CatalogEntry entryValue) {
        this.entryValue = entryValue;
    }

    @Override
    public Long getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object id) {
        this.entry= (Long) id;
    }

    public void setEntry(Long entry) {
        this.entry = entry;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public ApplicationItem getHandleValue() {
        return handleValue;
    }

    public void setHandleValue(ApplicationItem handleValue) {
        this.handleValue = handleValue;
    }

    @Override
    public Long getHandle() {
        return handle;
    }

    public void setHandle(Long handle) {
        this.handle = handle;
    }
}
