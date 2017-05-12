package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.server.chain.command.SynthesizeSolutionEntry;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by rarl on 11/05/17.
 */
public class SynthesizeSolutionEntryImpl implements SynthesizeSolutionEntry {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

    private final TaskRunnerPlugin plugin;

    @Inject
    public SynthesizeSolutionEntryImpl(TaskRunnerPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean execute(Context ctx) throws Exception {
        ActivityContext context = (ActivityContext) ctx;
        log.info("Thinking...");
        Model model = plugin.getSolver().resolveProblemContext(context);

        Variable[] variables = model.getVars();

        Arrays.stream(variables).forEachOrdered(v -> System.out.println(v));
        //FIXME actually synthesize the correct catalog entry so it can be commited later
        return CONTINUE_PROCESSING;
    }
}
