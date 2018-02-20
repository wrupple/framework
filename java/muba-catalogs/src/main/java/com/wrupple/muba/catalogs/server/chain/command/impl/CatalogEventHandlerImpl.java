package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogEventHandler;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CatalogEventHandlerImpl extends CatalogActionTriggerHandlerImpl implements CatalogEventHandler {
    @Inject
    public CatalogEventHandlerImpl(CatalogTriggerInterpret interpret) {
        super(interpret);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        CatalogActionContext context= (CatalogActionContext) ctx;
        //Advise = false (AFTER)
        CatalogDescriptor catalog = context.getCatalogDescriptor();
        // AFTER
        try {
            boolean r = extecute(context, false);

            return r;
        } catch (Exception e) {
            log.error("[FATAL TRIGGER ERROR]", e);
            throw new RuntimeException(e);
        }

    }
}
