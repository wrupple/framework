package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import com.wrupple.muba.bpm.server.service.ChocoPlugin;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.chocosolver.solver.Model;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ChocoPluginImpl implements ChocoPlugin {

    private final Provider<VariableEligibility> eligibilityProvider;
    private final ChocoModelResolver delegate;

    @Inject
    public ChocoPluginImpl(Provider<VariableEligibility> eligibilityProvider, ChocoModelResolver delegate) {
        this.eligibilityProvider = eligibilityProvider;
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
        return eligibilityProvider.get().of(field,context);
    }

    @Override
    public boolean solve(ApplicationContext context) {
        Model model = delegate.resolveSolverModel(context);
        return model.getSolver().solve();
    }
}
