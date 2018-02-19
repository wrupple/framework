package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogContract;
import com.wrupple.muba.catalogs.server.chain.command.CatalogEventInterpret;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;

import javax.inject.Provider;

public class CatalogEventInterpretImpl  implements CatalogEventInterpret {


    @Override
    public boolean execute(RuntimeContext ctx) throws Exception {


        return CONTINUE_PROCESSING;
    }

    @Override
    public Provider<CatalogActionContext> getProvider(RuntimeContext runtime) {
        return new Provider<CatalogActionContext>() {
            @Override
            public CatalogActionContext get() {
                CatalogContract event = /*(CatalogActionCommit)*/ (CatalogContract) runtime.getServiceContract();
                if (event.getStateValue() == null) {
                    //TODO remote (relative to the event source) listeners should read de actionRequest to create a mirrored action context (?)
                    throw new NullPointerException("No catalog action context marked as source of catalog event");
                }else{
                    return (CatalogActionContext) event.getStateValue();
                }
            }
        };
    }
}
