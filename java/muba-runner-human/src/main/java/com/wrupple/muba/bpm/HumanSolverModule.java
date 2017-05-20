package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.impl.AsyncHumanTaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.impl.DelegatingTaskRunnerEngine;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.vegetate.server.chain.AsyncTaskRunnerEngine;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(TaskRunnerEngine.class).to(DelegatingTaskRunnerEngine.class);
        //this provides the "model" or in this case human interaction context
        bind(Solver.class).to(SolverImpl.class).in(Singleton.class);


    }
}
