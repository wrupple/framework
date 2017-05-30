package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptorImpl;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 26/05/17.
 */
@Singleton
public class HumanSolverImpl implements Solver {

    private final FieldConversionStrategy access;

    @Inject
    public HumanSolverImpl(FieldConversionStrategy access) {
        this.access = access;
    }


    @Override
    public <T> T resolveProblemContext(ActivityContext context) {
        return (T) context;
    }

    @Override
    public boolean isEligible(FieldDescriptor field, ActivityContext context) {
        return field.isWriteable();
    }

    @Override
    public VariableDescriptor createVariable(FieldDescriptor field, ActivityContext context) {
        return new VariableDescriptorImpl(field);
    }

    @Override
    public void assignVariableValue(VariableDescriptor variable, String userInput) {
        Object intputValue = access.convertToPersistentDatabaseValue(userInput, variable.getField());
        ((VariableDescriptorImpl)variable).setValue(intputValue);
    }
}
