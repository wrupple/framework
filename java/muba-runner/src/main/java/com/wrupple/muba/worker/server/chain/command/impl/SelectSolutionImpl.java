package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.server.chain.command.SelectSolution;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SelectSolutionImpl implements SelectSolution {

    protected Logger log = LoggerFactory.getLogger(SelectSolutionImpl.class);

    private final SolverCatalogPlugin plugin;

    @Inject
    public SelectSolutionImpl(SolverCatalogPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        log.info("Selecting best solution");
        ApplicationContext context = (ApplicationContext) ctx;

        Long runnerId = context.getContainerStateValue().getRunner();

        if (runnerId == null) {
            log.debug("no main runner selected for container");
        } else {

            CatalogDescriptor solutionDescriptor = context.getStateValue().getCatalogValue();

            List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();

            Long onlyRunner = null;

            for (VariableDescriptor v : variableDescriptors) {
                if (onlyRunner == null || onlyRunner.equals(v.getRunner())) {

                } else {
                    //solution comes from many runners
                    onlyRunner = null;
                }
                onlyRunner = v.getRunner();
            }

            if (onlyRunner == null) {
                List<VariableDescriptor> requiredVariables = new ArrayList<>(solutionDescriptor.getFieldsValues().size());
                for (VariableDescriptor v : variableDescriptors) {
                    if (v.getRunner().equals(runnerId)) {
                        requiredVariables.add(v);
                    } else {
                        log.info("drop variable {} from unwanted runner {}", v.getField().getFieldId(), v.getRunner());
                    }
                }
                if (requiredVariables.size() == 0) {
                    log.warn("no variables selected");
                }
                context.getStateValue().setSolutionVariablesValues(requiredVariables);
            } else {
                if (!onlyRunner.equals(runnerId)) {
                    throw new IllegalStateException("Main runner produced no solucit");
                }
            }
        }


        return CONTINUE_PROCESSING;
    }
}
