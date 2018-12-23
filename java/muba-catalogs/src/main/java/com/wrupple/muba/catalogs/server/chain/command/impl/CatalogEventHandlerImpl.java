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
    public boolean execute(CatalogActionContext context) throws Exception {
        try {
            boolean r = extecute(context, false/*After*/);

            return r;
        } catch (Exception e) {
            log.error("Unable to interpret catalog event");
            throw new RuntimeException(e);
        }

    }
}
