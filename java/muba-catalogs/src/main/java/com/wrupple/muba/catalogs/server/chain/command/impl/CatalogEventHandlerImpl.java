package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.server.chain.command.CatalogEventHandler;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CatalogEventHandlerImpl extends CatalogActionTriggerHandlerImpl implements CatalogEventHandler {
    @Inject
    public CatalogEventHandlerImpl(CatalogTriggerInterpret interpret) {
        super(interpret);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        log.trace("<{}>",this.getClass().getSimpleName());
        CatalogActionContext context= (CatalogActionContext) ctx;
        //Advise = false (AFTER)
        CatalogDescriptor catalog = context.getCatalogDescriptor();
        // AFTER
        try {
            boolean r = extecute(context, false);
            log.trace("</{}>",this.getClass().getSimpleName());
            return r;
        } catch (Exception e) {
            log.error("[FATAL TRIGGER ERROR]", e);
            throw new RuntimeException(e);
        }

    }
}
