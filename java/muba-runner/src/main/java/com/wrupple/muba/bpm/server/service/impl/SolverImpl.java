package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.Runner;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.VariableConsensus;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.event.domain.FieldDescriptor;
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



    protected Logger log = LoggerFactory.getLogger(SolverImpl.class);


    private final List<Runner> runners;
    private final VariableConsensus reducer;

    @Inject
    public SolverImpl(VariableConsensus reducer) {
        this.runners =new ArrayList<>(3);
        this.reducer = reducer;
    }

    @Override
    public boolean solve(ApplicationContext context) {

        return runners.stream().
                map(plugin -> plugin.solve(context)).
                reduce( false,(a, b) -> a || b);
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
        if(eligible.isPresent()){
            VariableEligibility eligibility = eligible.get().handleAsVariable(field, context);
            if (log.isDebugEnabled()) {
                log.debug("[ {} is solvable] future: {}", field.getFieldId(), eligibility.toString());
            }
            return eligibility;
        }else{
            log.debug("[{} not solvable]", field.getFieldId());
            return null;
        }
    }



}
