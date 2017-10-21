package com.wrupple.muba.bpm.server.service;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.event.domain.FieldDescriptor;

/**
 * Created by rarl on 11/05/17.
 */
public interface Solver {

    VariableEligibility isEligible(FieldDescriptor field,ApplicationContext context);

    /**
     *
     * @param context
     * @return true if a solution has been found
     */
    boolean solve(ApplicationContext context);

    void register(Runner plugin);
}
