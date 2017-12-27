package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import org.chocosolver.solver.Model;

public interface ChocoModelResolver {
    Model resolveSolverModel(ApplicationContext context);
}
