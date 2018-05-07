package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogCreateTransactionImpl;
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


        WorkerState container = context.getStateValue().getWorkerStateValue();
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


        }else{
            log.info("Worker state will be updated");

            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(container.getId());
        }

        /*

        change application state
        request= new CatalogActionRequestImpl();
        request.setCatalog(applicationState.getCatalogType());
        request.setEntryValue(applicationState);
        request.setFollowReferences(true);


        if(applicationState.getId()==null){
            log.debug("New application state will be created");
            request.setName(CatalogActionRequest.CREATE_ACTION);

        }else{
            log.debug("current application {} will be updated",applicationState.getId());
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(applicationState.getId());

        }

        */



        container = context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);
        context.setStateValue(container.getStateValue());



        context.getRuntimeContext().setResult(container.getStateValue());

        return CONTINUE_PROCESSING;
    }
}
