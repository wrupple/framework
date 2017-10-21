package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.chain.command.SolveTask;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

    private final ProcessManager plugin;

    @Inject
    public SolveTaskImpl(ProcessManager plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        log.info("Thinking synchronously...");

        if(plugin.getSolver().solve(context)){

        }
        /*else if(model.getSolver().hasReachedLimit()){
            //System.out.println("The could not find a solution nor prove that none exists in the given limits");
        }*/else{
            throw new IllegalStateException("No viable solution found for problem");
        }
        return CONTINUE_PROCESSING;
    }
}
