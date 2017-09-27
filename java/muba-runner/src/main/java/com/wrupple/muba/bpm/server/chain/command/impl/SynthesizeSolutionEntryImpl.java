package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.server.chain.command.SynthesizeSolutionEntry;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SynthesizeSolutionEntryImpl implements SynthesizeSolutionEntry {
    protected Logger log = LoggerFactory.getLogger(SynthesizeSolutionEntryImpl.class);

    private final SolverCatalogPlugin plugin;
    private final SystemCatalogPlugin catalog;

    @Inject
    public SynthesizeSolutionEntryImpl(SolverCatalogPlugin plugin, SystemCatalogPlugin catalog) {
        this.plugin = plugin;
        this.catalog=catalog;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        log.info("Synthesize solution...");
        CatalogDescriptor solutionDescriptor = context.getSolutionDescriptor();
        CatalogEntry solution = catalog.access().synthesize(solutionDescriptor);

        List<VariableDescriptor> variableDescriptors = context.getSolutionVariables();

        log.trace("solution has {} variables",variableDescriptors.size());

        Instrospection solutionWritingInstrospection = catalog.access().newSession(solution);

        FieldDescriptor fieldId;
        Object fieldValue;
        for(VariableDescriptor solutionVariable : variableDescriptors){
            fieldId = solutionVariable.getField();
            fieldValue = solutionVariable.getValue();
            log.debug("    {}={}",fieldId.getFieldId(),fieldValue);
            catalog.access().setPropertyValue(fieldId,solution,fieldValue, solutionWritingInstrospection);
        }

        context.getRuntimeContext().setResult(solution);
        return CONTINUE_PROCESSING;
    }
}
