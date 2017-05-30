package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 * Created by rarl on 30/05/17.
 */
public class VariableDescriptorImpl implements VariableDescriptor {
    private final FieldDescriptor field;
    private Object value;

    public VariableDescriptorImpl(FieldDescriptor field) {
        this.field = field;
    }


    @Override
    public FieldDescriptor getField() {
        return field;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
