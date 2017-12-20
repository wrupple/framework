package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.bpm.server.chain.command.SolveTask;
import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import com.wrupple.muba.bpm.server.chain.command.impl.DefineSolutionCriteriaImpl;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.impl.HumanSolverImpl;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.bpm.shared.services.impl.FieldConversionStrategyImpl;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule{
    @Override
    protected void configure() {
        //this provides the "model" or in this case human interaction context
        bind(Solver.class).to(HumanSolverImpl.class).in(Singleton.class);

        bind(SolveTask.class).to(UserInteractionState.class);

        bind(FieldConversionStrategy.class).to(FieldConversionStrategyImpl.class);

        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class);

    }
}
