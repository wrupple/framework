package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface VariableEligibility {

    VariableEligibility of(FieldDescriptor field,ApplicationContext context);

    VariableDescriptor createVariable();
}
