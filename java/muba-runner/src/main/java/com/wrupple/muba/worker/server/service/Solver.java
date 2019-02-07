package com.wrupple.muba.worker.server.service;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.Operation;
import com.wrupple.muba.worker.domain.ApplicationContext;

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
    <T extends CatalogEntry> boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callcback) throws Exception;

    void register(Runner plugin);

    void model(Operation result, ApplicationContext context, Instrospection intros);

    void prepare(ApplicationContext context);
}
