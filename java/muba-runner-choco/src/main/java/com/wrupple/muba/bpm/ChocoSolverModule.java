package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import com.wrupple.muba.bpm.server.service.ChocoRunner;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.bpm.server.service.impl.ChocoModelResolverImpl;
import com.wrupple.muba.bpm.server.service.impl.ChocoRunnerImpl;
import com.wrupple.muba.bpm.server.service.impl.FutureChocoVariable;

/**
 * Created by japi on 11/05/17.
 */
public class ChocoSolverModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(ChocoRunner.class).to(ChocoRunnerImpl.class);
        bind(VariableEligibility.class).to(FutureChocoVariable.class);
        bind(ChocoModelResolver.class).to(ChocoModelResolverImpl.class);
    }
}
