package com.wrupple.muba.worker.shared.services;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.VariableEligibility;

public interface HumanVariableEligibility extends VariableEligibility {
    VariableEligibility of(FieldDescriptor field, ApplicationContext context);
}
