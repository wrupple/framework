package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.FieldDescriptor;

/**
 * Created by rarl on 17/05/17.
 */
public interface VariableDescriptor {
    FieldDescriptor getField();

    Object getValue();
}
