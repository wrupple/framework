package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.server.chain.command.SynthesizeSolutionEntry;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SynthesizeSolutionEntryImpl implements SynthesizeSolutionEntry {
    protected Logger log = LoggerFactory.getLogger(SynthesizeSolutionEntryImpl.class);

    private final TaskRunnerPlugin plugin;
    private final SystemCatalogPlugin catalog;

    @Inject
    public SynthesizeSolutionEntryImpl(TaskRunnerPlugin plugin,SystemCatalogPlugin catalog) {
        this.plugin = plugin;
        this.catalog=catalog;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ActivityContext context = (ActivityContext) ctx;
        log.info("Synthesize solution...");
        CatalogDescriptor solutionDescriptor = context.getSolutionDescriptor();
        CatalogEntry solution = catalog.synthesize(solutionDescriptor);

        Model model = plugin.getSolver().resolveProblemContext(context);

        List<VariableDescriptor> variableDescriptors = context.getSolutionVariables();

        log.debug("solution has {} variables",variableDescriptors.size());

        SystemCatalogPlugin.Session solutionWritingSession = catalog.newSession(solution);

        for(VariableDescriptor solutionVariable : variableDescriptors){
            catalog.setPropertyValue(solutionDescriptor,solutionVariable.getField(),solution,solutionVariable.getValue(),solutionWritingSession);
        }

        context.getExcecutionContext().setResult(solution);
        //FIXME actually synthesize the correct catalog entry so it can be commited later
        return CONTINUE_PROCESSING;
    }
}
