package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.TaskRunnerEngine;
import com.wrupple.muba.bpm.server.chain.command.UserInteractionState;
import com.wrupple.muba.bpm.server.chain.command.impl.PlainTextHumanSolverImpl;
import com.wrupple.muba.bpm.server.chain.command.impl.PlainTextUserInteractionState;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.impl.HumanSolverImpl;

/**
 * Created by japi on 11/05/17.
 */
public class HumanSolverModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(TaskRunnerEngine.class).to(PlainTextHumanSolverImpl.class);
        bind(UserInteractionState.class).to(PlainTextUserInteractionState.class);
        //this provides the "model" or in this case human interaction context
        bind(Solver.class).to(HumanSolverImpl.class).in(Singleton.class);


    }
}
