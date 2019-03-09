package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.VariableDescriptor;

import java.util.List;

public abstract class CatalogVariableDescriptor implements VariableDescriptor {

    public abstract CatalogEntry getForeignKeyValue();
    public abstract List<CatalogEntry> getForeignKeyValues();

    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
    }

    @Override
    public void setResult(Object r ) {
        throw new RuntimeException("No se debe alterar el resultado de una ejecucion en curso");
    }

}
