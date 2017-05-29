package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

/**
 * Created by rarl on 11/05/17.
 */
public interface Solver {
    <T> T resolveProblemContext(ActivityContext context);

    boolean isEligible(FieldDescriptor field,ActivityContext context);

    VariableDescriptor createVariable(FieldDescriptor field,ActivityContext context);

    void assignVariableValue(VariableDescriptor variable, String userInput);
}
