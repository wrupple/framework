package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public interface ChocoModelResolver {
    Model resolveSolverModel(ApplicationContext context);

    void clearModel(ApplicationContext context);
}
