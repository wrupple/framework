package com.wrupple.muba.worker.shared.services;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;

public interface HumanVariableEligibility extends VariableEligibility {
    VariableEligibility of(FieldDescriptor field, HumanApplicationContext context);
}
