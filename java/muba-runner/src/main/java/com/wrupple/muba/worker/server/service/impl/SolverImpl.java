package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.service.*;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by rarl on 11/05/17.
 */

@Singleton
public class SolverImpl implements Solver {


    private final List<Runner> runners;
    private final VariableConsensus reducer;
    protected Logger log = LoggerFactory.getLogger(SolverImpl.class);

    @Inject
    public SolverImpl(VariableConsensus reducer) {
        this.runners = new ArrayList<>(3);
        this.reducer = reducer;
    }



    @Override
    public void register(Runner plugin) {
        log.info("[RUNNER REGISTRATION] {}", plugin);
        runners.add(plugin);
    }

    public VariableEligibility isEligible(FieldDescriptor field, ApplicationContext context) {
        Optional<Runner> eligible = runners.stream().
                filter(
                        plugin -> plugin.canHandle(field, context)
                ).reduce(reducer);
        if (eligible.isPresent()) {
            VariableEligibility eligibility = eligible.get().handleAsVariable(field, context);
            if (log.isDebugEnabled()) {
                log.debug("[ {} is solvable] future: {}", field.getFieldId(), eligibility.toString());
            }
            return eligibility;
        } else {
            log.debug("[{} not solvable]", field.getFieldId());
            return null;
        }
    }

    @Override
    public <T extends CatalogEntry> boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callcback) {

        //TODO configure: all runners must return, first one, wait for main runner (default)
        ForkCallback<ApplicationContext> fork = new ForkCallback<ApplicationContext>(callcback);

        if (runners.isEmpty()) {
            log.warn("no runners to solve task");
            return Command.PROCESSING_COMPLETE;
        }

        List<StateTransition<ApplicationContext>> callbacks = new ArrayList<>(runners.size());
        for (Runner plugin : runners) {
            /* FIXME wrap in a callback that fires an event when a runner finds a complete solution*/
            callbacks.add(fork.fork());

        }

        Runner plugin;
        for (int i = 0; i < runners.size(); i++) {
            plugin = runners.get(i);
            plugin.solve(context, callbacks.get(i));

        }


        return Command.CONTINUE_PROCESSING;
    }


}