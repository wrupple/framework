package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.WorkerState;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.UpdateApplicationContext;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by japi on 18/08/17.
 */
public class UpdateApplicationContextImpl implements UpdateApplicationContext {
    protected static final Logger log = LogManager.getLogger(UpdateApplicationContextImpl.class);

    @Override
    public boolean execute(Context  ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;

        ApplicationState state = context.getStateValue();
        WorkerState container = state.getWorkerStateValue();
        if (container == null) {
            throw new IllegalStateException("No application container");
        }

        CatalogActionRequestImpl request= new CatalogActionRequestImpl();
        request.setCatalog(container.getCatalogType());
        request.setEntryValue(container);
        request.setFollowReferences(true);

        if(container.getId()==null){
            log.info("New worker will be created");
            request.setName(CatalogActionRequest.CREATE_ACTION);
            //com.wrupple.muba.event.server.chain.Publish
            container = context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);
        }else{
            log.info("worker state will be updated");
            //com.wrupple.muba.desktop.client.chain.command.InstallActivityEventHandler
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(container.getId());
            container = context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);

            //TODO updating working should result in underlying application beeing updated too
            log.info("application state will be updated");
            request= new CatalogActionRequestImpl();
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setCatalog(ApplicationState.CATALOG);
            request.setEntry(state.getId());
            request.setEntryValue(state);
            state = context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);

        }

        if(state==null){
            throw new IllegalStateException("No application to run");
        }else if(state.getWorkerStateValue()==null){
            throw new IllegalStateException("Application belongs to no container");
        }

        if(container==null){
            throw new IllegalStateException("No application container");
        }else if(container.getStateValue()==null){
            throw new IllegalStateException("Container has no application assigned");
        }

        context.setStateValue(state);

        context.getRuntimeContext().setResult(state);

        return CONTINUE_PROCESSING;
    }
}
