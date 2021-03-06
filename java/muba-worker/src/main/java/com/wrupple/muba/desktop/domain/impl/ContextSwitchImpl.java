package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

public class ContextSwitchImpl extends CatalogEntryImpl implements ContextSwitch {


    @ForeignKey(foreignCatalog =  WorkerState.CATALOG)
    private Long order;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = WorkerState.CATALOG)
    private WorkerState workerStateValue;


    @Override
    public WorkerState getWorkerStateValue() {
        return workerStateValue;
    }



    public void setWorkerStateValue(WorkerState orderValue) {
        this.workerStateValue = orderValue;
    }


    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public Object getCatalog() {
        return getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }


    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
