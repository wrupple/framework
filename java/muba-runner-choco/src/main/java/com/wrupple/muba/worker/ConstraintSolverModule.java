package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.impl.ApplicationContextImpl;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import com.wrupple.muba.worker.server.service.ChocoRunner;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import com.wrupple.muba.worker.server.service.impl.ChocoModelResolverImpl;
import com.wrupple.muba.worker.server.service.impl.ChocoRunnerImpl;
import com.wrupple.muba.worker.server.service.impl.FutureChocoVariable;

/**
 * Created by japi on 11/05/17.
 */
public class ConstraintSolverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ApplicationContext.class).to(ApplicationContextImpl.class);

        bind(String.class).annotatedWith(Names.named("choco.model.key")).toInstance("_chocomodel");

        bind(ChocoRunner.class).to(ChocoRunnerImpl.class);
        bind(VariableEligibility.class).to(FutureChocoVariable.class);
        bind(ChocoModelResolver.class).to(ChocoModelResolverImpl.class);
    }
}
