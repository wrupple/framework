package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 * Created by rarl on 11/05/17.
 */
public interface Solver {
    <T> T resolveSolverModel(ApplicationContext context);

    boolean isEligible(FieldDescriptor field,ApplicationContext context);

    VariableDescriptor createVariable(FieldDescriptor field,ApplicationContext context);

    void assignVariableValue(VariableDescriptor variable, String userInput);
}
