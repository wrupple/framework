package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.chain.command.UserInteractionState;
import com.wrupple.muba.worker.server.chain.command.impl.DefineSolutionCriteriaImpl;
import com.wrupple.muba.worker.server.service.Solver;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;
import com.wrupple.muba.worker.shared.services.impl.FieldConversionStrategyImpl;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule {
    @Override
    protected void configure() {
        //this provides the "model" or in this case human interaction context
        bind(Solver.class).to(HumanSolverImpl.class).in(Singleton.class);

        bind(SolveTask.class).to(UserInteractionState.class);

        bind(FieldConversionStrategy.class).to(FieldConversionStrategyImpl.class);

        bind(DefineSolutionCriteria.class).to(DefineSolutionCriteriaImpl.class);

    }
}
