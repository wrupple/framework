package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.domain.BusinessIntent;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by japi on 12/08/17.
 */
public class BusinessIntentImpl extends ManagedObjectImpl implements BusinessIntent {
    @ForeignKey(
            foreignCatalog = ApplicationState.CATALOG
    )
    private Long state;
    @CatalogValue(
            foreignCatalog = ApplicationState.CATALOG
    )
    @CatalogField(ignore = true)
    @NotNull
    private ApplicationState stateValue;

    private Object entry;
    private CatalogEntry entryValue;
    private String catalog;

    @Override
    public String getCatalogType() {
        return BusinessIntent_CATALOG;
    }

    public Long getState() {
        return state;
    }

    @Override
    public Object getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object entry) {
        this.entry = entry;
    }

    @Override
    public CatalogEntry getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(CatalogEntry entryValue) {
        this.entryValue = entryValue;
    }

    public void setState(Long state) {
        this.state = state;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }

    @Override
    public void setStateValue(ApplicationState applicationState) {
        this.stateValue=applicationState;
    }


    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

}
