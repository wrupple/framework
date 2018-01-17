package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.worker.domain.ApplicationContext;
import org.chocosolver.solver.Model;

public interface ChocoModelResolver {
    Model resolveSolverModel(ApplicationContext context);
}
