package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.VariableDescriptor;
import com.wrupple.muba.worker.server.chain.command.SynthesizeSolutionEntry;
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

    private final FieldAccessStrategy plugin;

    @Inject
    public SynthesizeSolutionEntryImpl(FieldAccessStrategy plugin) {
        this.plugin=plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        log.info("Synthesize solution...");
        CatalogDescriptor solutionDescriptor = context.getStateValue().getCatalogValue();
        CatalogEntry solution = plugin.synthesize(solutionDescriptor);

        List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();

        log.trace("solution has {} variables",variableDescriptors.size());

        Instrospection solutionWritingInstrospection = plugin.newSession(solution);

        FieldDescriptor fieldId;
        Object fieldValue;
        for(VariableDescriptor solutionVariable : variableDescriptors){
            fieldId = solutionVariable.getField();
            fieldValue = solutionVariable.getResult();
            log.debug("    {}={}",fieldId.getFieldId(),fieldValue);
            plugin.setPropertyValue(fieldId,solution,fieldValue, solutionWritingInstrospection);
        }

        context.getRuntimeContext().setResult(solution);
        return CONTINUE_PROCESSING;
    }
}
