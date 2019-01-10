package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SynthesizeSolutionEntry;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SynthesizeSolutionEntryImpl implements SynthesizeSolutionEntry {
    protected Logger log = LogManager.getLogger(SynthesizeSolutionEntryImpl.class);

    private final FieldAccessStrategy plugin;

    @Inject
    public SynthesizeSolutionEntryImpl(FieldAccessStrategy plugin) {
        this.plugin=plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        CatalogEntry solution = context.getStateValue().getEntryValue();
        log.info("Synthesize solution...");
        CatalogDescriptor solutionDescriptor = context.getStateValue().getCatalogValue();
        if(solution==null ){

            solution = plugin.synthesize(solutionDescriptor);

        }else{
            log.warn("appears solutions is already there");

        }


        List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();

        log.trace("solution has {} variables",variableDescriptors.size());

        Instrospection solutionWritingInstrospection = assertInstrospector(context);

        FieldDescriptor fieldId;
        Object fieldValue;
        for(VariableDescriptor solutionVariable : variableDescriptors){
            fieldId = solutionVariable.getField();
            fieldValue = solutionVariable.getResult();
            log.debug("    {}={}",fieldId.getDistinguishedName(),fieldValue);
            plugin.setPropertyValue(fieldId,solution,fieldValue, solutionWritingInstrospection);
        }

        context.getRuntimeContext().setResult(solution);
        return CONTINUE_PROCESSING;
    }



    private Instrospection assertInstrospector(ApplicationContext context) {
        Instrospection r = (Instrospection) context.get(INTROSPECTIONKEY);
        if(r==null){
            r = plugin.newSession(context.getStateValue().getEntryValue());
            context.put(INTROSPECTIONKEY,r);
        }
        return r;
    }

}
