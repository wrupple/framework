package com.wrupple.muba.bpm.server.chain.impl;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.WorkCompleteEvent;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.bpm.server.chain.command.GoToCommand;
import com.wrupple.muba.bpm.server.chain.command.NextPlace;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.generic.LookupCommand;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WorkflowEngineImpl extends LookupCommand implements WorkflowEngine {


    @Inject
    public WorkflowEngineImpl(CatalogFactory factory, GoToCommand goTo, ExplicitOutputPlace explicit, NextPlace next) {
        super(factory);
        super.setNameKey(CatalogEntry.NAME_FIELD);
        super.setCatalogName(WorkCompleteEvent.CATALOG/*FormatDictionary*/);
        Catalog c = new CatalogBase();
        c.addCommand(NEXT_APPLICATION_ITEM,next);
        c.addCommand(EXPLICIT_APPLICATION_ITEM,explicit);
        c.addCommand(GOTO_OUTPUT_ITEM,goTo);
        factory.addCatalog(WorkCompleteEvent.CATALOG,c);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {


        boolean r = super.execute(ctx);

        RuntimeContext context = (RuntimeContext) ctx;
        WorkCompleteEvent event = (WorkCompleteEvent) context.getServiceContract();
        ApplicationState state = (ApplicationState) event.getStateValue();
        Workflow newItem = (Workflow) state.getHandleValue();
        if(newItem.isClearOutput()){
            state.setEntryValue(null);
        }

        return r;
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
