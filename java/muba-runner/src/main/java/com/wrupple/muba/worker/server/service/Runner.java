package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;

public interface Runner {

    boolean canHandle(FieldDescriptor field, ApplicationContext context);

    VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context);

    boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) throws Exception;
}
