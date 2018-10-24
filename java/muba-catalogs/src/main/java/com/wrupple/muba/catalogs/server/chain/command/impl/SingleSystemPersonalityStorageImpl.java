package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.SystemPersonalitiesStorage;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SingleSystemPersonalityStorageImpl extends  SingleKeyProvider implements SystemPersonalitiesStorage {

    @Inject
    public SingleSystemPersonalityStorageImpl(@Named(SessionContext.SYSTEM) Person anon) {
       super(anon);
    }

}
