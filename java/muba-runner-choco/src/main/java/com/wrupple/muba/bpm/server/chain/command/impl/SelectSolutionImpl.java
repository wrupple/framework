package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.server.chain.command.SelectSolution;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SelectSolutionImpl implements SelectSolution {

    protected Logger log = LoggerFactory.getLogger(SelectSolutionImpl.class);

    private final TaskRunnerPlugin plugin;

    @Inject
    public SelectSolutionImpl(TaskRunnerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ActivityContext context = (ActivityContext) ctx;
        Model model = plugin.getSolver().resolveProblemContext(context);
        log.info("{} solution(s) have been found",model.getSolver().getSolutionCount());

        return CONTINUE_PROCESSING;
    }
}
