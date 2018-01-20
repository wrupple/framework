package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.domain.reserved.HasRunner;

/**
 * Created by rarl on 17/05/17.
 */
public interface VariableDescriptor extends HasResult<Object>, HasRunner {


    FieldDescriptor getField();

}
