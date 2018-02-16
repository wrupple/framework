package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.VariableDescriptor;

public abstract class AbstractVariableDescriptor implements VariableDescriptor {
    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
    }

    @Override
    public void setResult(Object r ) {
        throw new RuntimeException("No se debe alterar el resultado de una ejecucion en curso");
    }

}
