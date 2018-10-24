package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.KnownHostsProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class MonoliticLocal extends SingleKeyProvider implements KnownHostsProvider {

    @Inject
    public MonoliticLocal(@Named(SessionContext.SYSTEM) Host anon) {
        super(anon);
    }
}