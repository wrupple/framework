package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.command.CatalogEventInterpret;
import com.wrupple.muba.catalogs.domain.CatalogEvent;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Context;

public class CatalogEventInterpretImpl  implements CatalogEventInterpret {

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {

        CatalogEvent event = /*(CatalogActionCommit)*/ (CatalogEvent) requestContext.getServiceContract();
        if (event.getLiveContext() == null) {
            //TODO remote (relative to the event source) listeners should read de actionRequest to create a mirrored action context (?)
            throw new NullPointerException("No catalog action context marked as source of catalog event");
        }else{
            return event.getLiveContext();
        }
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        /*

        NOTHING TO DO

        RuntimeContext requestContext = (RuntimeContext) ctx;
        CatalogActionCommit request = (CatalogActionCommit) requestContext.getServiceContract();
        CatalogActionContext context = requestContext.getServiceContext();

        */

        return CONTINUE_PROCESSING;
    }
}
