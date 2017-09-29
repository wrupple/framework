package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionFilterEngine;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class CatalogActionFilterEngineImpl extends CatalogActionTriggerHandlerImpl implements CatalogActionFilterEngine {


   @Inject public CatalogActionFilterEngineImpl(CatalogTriggerInterpret interpret) {
        super(interpret);
    }

    @Override
    public boolean execute(Context c) throws Exception {
        return extecute((CatalogActionContext) c, true);
    }
}
