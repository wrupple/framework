package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.impl.SolverImpl;

/**
 * Created by japi on 11/05/17.
 */
public class ChocoSolverModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(Solver.class).to(SolverImpl.class).in(Singleton.class);

        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class).in(Singleton.class);
        bind(SolveTask.class).to(SolveTaskImpl.class);

    }
}