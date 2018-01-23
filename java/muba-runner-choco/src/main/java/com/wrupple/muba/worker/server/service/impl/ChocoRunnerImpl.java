package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import com.wrupple.muba.worker.server.service.ChocoRunner;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import org.chocosolver.solver.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class ChocoRunnerImpl implements ChocoRunner {
    protected Logger log = LoggerFactory.getLogger(ChocoRunnerImpl.class);

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
    public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) {
        Model model = delegate.resolveSolverModel(context);
         /*else if(model.getSolver().hasReachedLimit()){
            //System.out.println("The could not find a solution nor prove that none exists in the given limits");
        }*/
        log.info("Solving...");
        boolean retorno = model.getSolver().solve();
        try {
            callback.execute(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return retorno;
    }


}
