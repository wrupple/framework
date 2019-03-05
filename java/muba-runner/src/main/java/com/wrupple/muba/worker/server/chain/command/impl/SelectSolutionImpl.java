package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.server.chain.command.SelectSolution;
import com.wrupple.muba.worker.server.service.Solver;
import com.wrupple.muba.worker.server.service.VariableConsensus;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SelectSolutionImpl implements SelectSolution {

    protected Logger log = LogManager.getLogger(SelectSolutionImpl.class);

    private final Solver consensus;

    @Inject
    public SelectSolutionImpl(Solver consensus) {
        this.consensus = consensus;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        log.info("Selecting best solution");
        ApplicationContext context = (ApplicationContext) ctx;

            CatalogDescriptor solutionDescriptor = context.getStateValue().getCatalogValue();

            List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();


                List<VariableDescriptor> requiredVariables = new ArrayList<>(solutionDescriptor.getFieldsValues().size());
                for (VariableDescriptor v : variableDescriptors) {
                    if (v.isSolved()) {
                        requiredVariables.add(v);
                    } else {
                        log.info("drop variable {} from unwanted runner {}", v.getField().getDistinguishedName(), v.getRunner());
                    }
                }
                if (requiredVariables.size() == 0) {
                    log.warn("no variables selected");
                }
                context.getStateValue().setSolutionVariablesValues(requiredVariables);

        consensus.onProblemSolved(context);

        return CONTINUE_PROCESSING;
    }

    private WorkerState getWorker(ApplicationContext context) {
        ApplicationState state = context.getStateValue();
        WorkerState worker =state.getWorkerStateValue();

        if(state==null){
            ApplicationState  eldest = CatalogEntryImpl.getRootAncestor(state);
            if(eldest!=null){
                worker = eldest.getWorkerStateValue();
            }
        }

        return worker;

    }
}
