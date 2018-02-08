package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.VariableDescriptor;

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
