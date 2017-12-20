package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.HumanSolver;
import com.wrupple.muba.bpm.server.service.Runner;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.event.domain.FieldDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 26/05/17.
 */
@Singleton
public class HumanSolverImpl implements HumanSolver {

    private final FieldConversionStrategy access;

    @Inject
    public HumanSolverImpl(FieldConversionStrategy access) {
        this.access = access;
    }


    @Override
    public VariableEligibility isEligible(FieldDescriptor field, ApplicationContext context) {
        return null;
    }

    @Override
    public boolean solve(ApplicationContext context) {
        return false;
    }

    @Override
    public void register(Runner plugin) {

    }
}
