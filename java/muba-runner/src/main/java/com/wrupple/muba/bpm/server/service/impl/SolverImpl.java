package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.server.service.SolverPlugin;
import com.wrupple.muba.bpm.server.service.VariableConsensus;
import com.wrupple.muba.bpm.server.service.VariableEligibility;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.event.domain.FieldDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by rarl on 11/05/17.
 */

public class SolverImpl implements Solver {



    protected Logger log = LoggerFactory.getLogger(SolverImpl.class);


    private final List<SolverPlugin> delegates;
    private final VariableConsensus reducer;

    @Inject
    public SolverImpl(VariableConsensus reducer) {
        this.delegates=new ArrayList<>(3);
        this.reducer = reducer;
    }

    @Override
    public boolean solve(ApplicationContext context) {

        return delegates.stream().
                map(plugin -> plugin.solve(context)).
                reduce( false,(a, b) -> a || b);
    }

    @Override
    public void register(SolverPlugin plugin) {
        delegates.add(plugin);
    }

    public VariableEligibility isEligible(FieldDescriptor field, ApplicationContext context) {
        Optional<SolverPlugin> eligible = delegates.stream().
                filter(
                        plugin -> plugin.canHandle(field, context)
                ).reduce(reducer);
        if(eligible.isPresent()){
            return eligible.get().handleAsVariable(field,context);
        }else{
            return null;
        }
    }



}
