package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.event.domain.FieldDescriptor;

public interface SolverPlugin {

    boolean canHandle(FieldDescriptor field, ApplicationContext context);

    VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context);

    boolean solve(ApplicationContext context);
}
