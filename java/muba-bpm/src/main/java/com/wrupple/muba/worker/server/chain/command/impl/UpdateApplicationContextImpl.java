package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ApplicationState;
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
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(applicationState.getId());
            request.setEntryValue(applicationState);
        request.setFollowReferences(true);
        List results=context.getRuntimeContext().getEventBus().fireEvent(request,context.getRuntimeContext(),null);

        applicationState = (ApplicationState) results.get(0);
        context.setStateValue(applicationState);
            //commit
            context.getRuntimeContext().setResult(applicationState/* request.getEntryValue()*/);
        return CONTINUE_PROCESSING;
    }
}
