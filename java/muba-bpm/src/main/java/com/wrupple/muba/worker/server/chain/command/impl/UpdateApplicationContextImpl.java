package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.UpdateApplicationContext;
import org.apache.commons.chain.Context;

import java.util.List;

/**
 * Created by japi on 18/08/17.
 */
public class UpdateApplicationContextImpl implements UpdateApplicationContext {
    @Override
    public boolean execute(Context  ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
            ApplicationState applicationState = context.getStateValue();
        CatalogActionRequestImpl request= new CatalogActionRequestImpl();
        //FIXME update application context of the right type
        request.setCatalog(ApplicationState.CATALOG);
        request.setEntryValue(applicationState);
        request.setFollowReferences(true);

            if(applicationState.getId()==null){

                request.setName(CatalogActionRequest.CREATE_ACTION);

            }else{

                request.setName(CatalogActionRequest.WRITE_ACTION);
                request.setEntry(applicationState.getId());

            }

        List results=context.getRuntimeContext().getEventBus().fireEvent(request,context.getRuntimeContext(),null);

        applicationState = (ApplicationState) results.get(0);
        context.setStateValue(applicationState);
        context.getRuntimeContext().setResult(applicationState);

        return CONTINUE_PROCESSING;
    }
}
