package com.wrupple.muba.worker.server.chain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.worker.server.chain.WorkflowEngine;
import com.wrupple.muba.worker.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.worker.server.chain.command.GoToCommand;
import com.wrupple.muba.worker.server.chain.command.NextPlace;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class WorkflowEngineImpl extends LookupCommand implements WorkflowEngine {


    @Inject
    public WorkflowEngineImpl(CatalogFactory factory, GoToCommand goTo, ExplicitOutputPlace explicit, NextPlace next,@Named("bpm.dictionary.outputHandler") String outputHandlerDictionary) {
        super(factory);
        super.setNameKey(CatalogEntry.NAME_FIELD);
        super.setCatalogName(outputHandlerDictionary);
        Catalog c = new CatalogBase();
        c.addCommand(NEXT_APPLICATION_ITEM,next);
        c.addCommand(EXPLICIT_APPLICATION_ITEM,explicit);
        c.addCommand(GOTO_OUTPUT_ITEM,goTo);
        factory.addCatalog(outputHandlerDictionary,c);
    }


   /* @Override
    public boolean execute(Context context) throws Exception {


        WorkCompleteEvent event = NULL;

                GWTUtils.setAttribute(properties, outputHandlerRegistry.getPropertyName(), command);
        OutputHandler service = outputHandlerRegistry.getConfigured(properties, processContext, eventBus, processParameters);

        assert service != null : "No command '" + rawCommand + "' found";

        service.prepare(rawCommand, properties, eventBus, processContext,
                processParameters, callback);
        service.execute();


        return CONTINUE_PROCESSING;
    }*/
}
