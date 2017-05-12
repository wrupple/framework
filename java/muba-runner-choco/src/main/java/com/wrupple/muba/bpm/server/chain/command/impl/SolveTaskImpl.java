package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.SolveTask;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by rarl on 11/05/17.
 */
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

    private final TaskRunnerPlugin plugin;

    @Inject
    public SolveTaskImpl(TaskRunnerPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean execute(Context ctx) throws Exception {
        ActivityContext context = (ActivityContext) ctx;
        log.info("Thinking...");
        Model model = plugin.getSolver().resolveProblemContext(context);
        if(model.getSolver().solve()){
            log.info("At least one solution found");
            if(log.isDebugEnabled()){
                model.getSolver().showSolutions();
            }
        }else{
            throw new IllegalStateException("No viable solution found for problem");
        }
        return CONTINUE_PROCESSING;
    }
}
