package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import com.wrupple.muba.bpm.server.service.ChocoRunner;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.chocosolver.solver.Model;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ChocoRunnerImpl implements ChocoRunner {

    private final Provider<FutureChocoVariable> future;
    private final ChocoModelResolver delegate;

    @Inject
    public ChocoRunnerImpl(Provider<FutureChocoVariable> future, ChocoModelResolver delegate) {
        this.future = future;
        this.delegate = delegate;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        //only integer fields with constraints or defined domains are eligible
        boolean eligibility = field.getDataType()== CatalogEntry.INTEGER_DATA_TYPE && ((field.getDefaultValueOptions()!=null && !field.getDefaultValueOptions().isEmpty())
                || (field.getConstraintsValues()!=null && !field.getConstraintsValues().isEmpty()));


        return eligibility;
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return future.get().of(field,context);
    }

    @Override
    public boolean solve(ApplicationContext context) {
        Model model = delegate.resolveSolverModel(context);
        return model.getSolver().solve();
    }
}
