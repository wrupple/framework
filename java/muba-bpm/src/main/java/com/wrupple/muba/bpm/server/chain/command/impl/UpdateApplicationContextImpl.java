package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.BusinessIntent;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.server.chain.command.UpdateApplicationContext;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 18/08/17.
 */
public class UpdateApplicationContextImpl implements UpdateApplicationContext {
    @Override
    public boolean execute(Context  ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
            BusinessIntent contractExplicitIntent = (BusinessIntent) context.getRuntimeContext().getServiceContract();
            ApplicationState applicationState = context.getRuntimeContext().getConvertedResult();

            applicationState.setEntryValue((CatalogEntry) context.getRuntimeContext().getServiceContract());

            CatalogActionRequestImpl request= new CatalogActionRequestImpl();
            //FIXME update application context of the right type
            request.setCatalog(ApplicationState.CATALOG);
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(applicationState.getId());
            request.setEntryValue(applicationState);

        applicationState=context.getRuntimeContext().getEventBus().fireEvent(request,context.getRuntimeContext(),null);

            //commit
            context.getRuntimeContext().setResult(applicationState/* request.getEntryValue()*/);
        return CONTINUE_PROCESSING;
    }
}