package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionCommit;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionFilterInterpret;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceContext;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;

public class CatalogActionFilterInterpretImpl implements CatalogActionFilterInterpret {
    protected static final Logger log = LoggerFactory.getLogger(CatalogActionFilterInterpretImpl.class);

    @Override
    public boolean execute(RuntimeContext requestContext) throws Exception {





        return CONTINUE_PROCESSING;
    }

    @Override
    public Provider<CatalogActionContext> getProvider(final RuntimeContext runtime) {
        return new Provider<CatalogActionContext>() {
            @Override
            public CatalogActionContext get() {
                CatalogActionCommit event = (CatalogActionCommit) runtime.getServiceContract();
                if (event.getStateValue() == null) {
                    //TODO remote (relative to the event source) listeners should read de actionRequest to create a mirrored action context (?)
                    //re-bind the service contract into a runtime context?
                    throw new NullPointerException("No catalog action context marked as source of commit event");
                }else{
                    return (CatalogActionContext) event.getStateValue();
                }
            }
        };
    }
}
