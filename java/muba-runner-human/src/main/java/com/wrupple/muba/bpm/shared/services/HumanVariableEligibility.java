package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface HumanVariableEligibility extends VariableEligibility {
    VariableEligibility of(FieldDescriptor field, ApplicationContext context);
}
