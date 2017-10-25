package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.wrupple.muba.catalogs.server.service.SQLDelegate;
import com.wrupple.muba.catalogs.server.service.impl.SQLDelegateImpl;

public class SQLModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SQLDelegate.class).to(SQLDelegateImpl.class);
    }

}
