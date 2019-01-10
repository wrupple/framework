package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Operation;

public abstract class OperationImpl extends CatalogEntryImpl implements Operation {

    private boolean modeled;

    @Override
    public boolean isModeled() {
        return modeled;
    }

    public void setModeled(boolean modeled) {
        this.modeled = modeled;
    }
}
