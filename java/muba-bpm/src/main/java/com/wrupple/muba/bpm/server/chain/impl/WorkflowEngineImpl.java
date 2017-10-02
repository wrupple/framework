package com.wrupple.muba.bpm.server.chain.impl;

import com.wrupple.muba.bpm.domain.WorkflowFinishedIntent;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.bpm.server.chain.command.GoToCommand;
import com.wrupple.muba.bpm.server.chain.command.NextPlace;
import com.wrupple.muba.event.domain.ExplicitIntent;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.generic.LookupCommand;

import javax.inject.Inject;

public class WorkflowEngineImpl extends LookupCommand implements WorkflowEngine {


    @Inject
    public WorkflowEngineImpl(CatalogFactory factory, GoToCommand goTo, ExplicitOutputPlace explicit, NextPlace next) {
        super(factory);
        super.setNameKey(ExplicitIntent.HANDLE_FIELD);
        super.setCatalogName(WorkflowFinishedIntent.CATALOG/*FormatDictionary*/);
        Catalog
        c = factory.getCatalog(WorkflowFinishedIntent.CATALOG);
        c.addCommand(NEXT_APPLICATION_ITEM,next);
        c.addCommand(EXPLICIT_APPLICATION_ITEM,explicit);
        c.addCommand(GOTO_OUTPUT_ITEM,goTo);
    }

   /* @Override
    public boolean execute(Context context) throws Exception {


        WorkflowFinishedIntent event = NULL;

                GWTUtils.setAttribute(properties, outputHandlerRegistry.getPropertyName(), command);
        OutputHandler service = outputHandlerRegistry.getConfigured(properties, processContext, eventBus, processParameters);

        assert service != null : "No command '" + rawCommand + "' found";

        service.prepare(rawCommand, properties, eventBus, processContext,
                processParameters, callback);
        service.execute();


        return CONTINUE_PROCESSING;
    }*/
}
