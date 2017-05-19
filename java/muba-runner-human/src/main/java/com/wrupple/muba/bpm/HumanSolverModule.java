package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.service.Solver;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(Solver.class).to(SolverImpl.class).in(Singleton.class);


        bind(DetermineSolutionFieldsDomain.class).to(DetermineSolutionFieldsDomainImpl.class).in(Singleton.class);
        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class).in(Singleton.class);
        bind(SolveTask.class).to(SolveTaskImpl.class);
        bind(SelectSolution.class).to(SelectSolutionImpl.class);
        bind(SynthesizeSolutionEntry.class).to(SynthesizeSolutionEntryImpl.class);
    }
}
