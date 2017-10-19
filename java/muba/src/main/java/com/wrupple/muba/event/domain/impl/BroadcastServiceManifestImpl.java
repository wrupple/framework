package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.BroadcastEvent;
import com.wrupple.muba.event.domain.BroadcastServiceManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class BroadcastServiceManifestImpl extends ServiceManifestImpl implements BroadcastServiceManifest {

    @Inject
    public BroadcastServiceManifestImpl(@Named(BroadcastEvent.CATALOG) CatalogDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogEntry.NAME_FIELD));
    }
}
