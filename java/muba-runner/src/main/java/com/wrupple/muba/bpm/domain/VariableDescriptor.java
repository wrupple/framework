package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 * Created by rarl on 17/05/17.
 */
public interface VariableDescriptor {
    public FieldDescriptor getField();
    public Object getValue();
}
