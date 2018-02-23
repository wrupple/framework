package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.UpdateApplicationContext;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 18/08/17.
 */
public class UpdateApplicationContextImpl implements UpdateApplicationContext {
    @Override
    public boolean execute(Context  ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;

        ApplicationState applicationState = context.getStateValue();
        CatalogActionRequestImpl request= new CatalogActionRequestImpl();
        request.setCatalog(applicationState.getCatalogType());
        request.setEntryValue(applicationState);
        request.setFollowReferences(true);

        if(applicationState.getId()==null){

            request.setName(CatalogActionRequest.CREATE_ACTION);
        }else{

            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(applicationState.getId());
        }


        applicationState = context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);
        context.setStateValue(applicationState);
        context.getRuntimeContext().setResult(applicationState);

        return CONTINUE_PROCESSING;
    }
}
