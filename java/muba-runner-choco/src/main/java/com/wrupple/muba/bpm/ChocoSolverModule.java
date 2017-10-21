package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.chain.command.*;
import com.wrupple.muba.bpm.server.chain.command.impl.*;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import com.wrupple.muba.bpm.server.service.ChocoPlugin;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.bpm.server.service.impl.ChocoModelResolverImpl;
import com.wrupple.muba.bpm.server.service.impl.ChocoPluginImpl;
import com.wrupple.muba.bpm.server.service.impl.SolverImpl;
import com.wrupple.muba.bpm.server.service.impl.VariableEligibilityImpl;

/**
 * Created by japi on 11/05/17.
 */
public class ChocoSolverModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(ChocoPlugin.class).to(ChocoPluginImpl.class);
        bind(VariableEligibility.class).to(VariableEligibilityImpl.class);
        bind(ChocoModelResolver.class).to(ChocoModelResolverImpl.class);
    }
}
