package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.server.chain.UpdateApplicationContext;
import com.wrupple.muba.bpm.server.domain.BusinessContext;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import org.apache.commons.chain.Context;

/**
 * Created by japi on 18/08/17.
 */
public class UpdateApplicationContextImpl implements UpdateApplicationContext {
    @Override
    public boolean execute(Context  ctx) throws Exception {
        BusinessContext context = (BusinessContext) ctx;
        if(context.isChanged()){
            BusinessEvent contractExplicitIntent = (BusinessEvent) context.getRuntimeContext().getServiceContract();
            ApplicationState applicationState = context.getRuntimeContext().getConvertedResult();

            applicationState.setEntryValue((CatalogEntry) context.getRuntimeContext().getServiceContract());

            CatalogActionRequestImpl request= new CatalogActionRequestImpl();
            //FIXME update application context of the right type
            request.setCatalog(ApplicationState.CATALOG);
            request.setName(CatalogActionRequest.WRITE_ACTION);
            request.setEntry(applicationState.getId());
            request.setEntryValue(applicationState);
            //commit
            context.getRuntimeContext().setResult(context.getRuntimeContext().spawnProcess(request));
        }
        return CONTINUE_PROCESSING;
    }
}
