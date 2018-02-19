package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogContract;
import com.wrupple.muba.catalogs.server.chain.command.CatalogEventInterpret;
import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.Context;

public class CatalogEventInterpretImpl  implements CatalogEventInterpret {

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {

        CatalogContract event = /*(CatalogActionCommit)*/ (CatalogContract) requestContext.getServiceContract();
        if (event.getStateValue() == null) {
            //TODO remote (relative to the event source) listeners should read de actionRequest to create a mirrored action context (?)
            throw new NullPointerException("No catalog action context marked as source of catalog event");
        }else{
            return event.getStateValue();
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
