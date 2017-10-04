package com.wrupple.muba.event.domain;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

@Singleton
public class BroadcastServiceManifestImpl extends ServiceManifestImpl implements BroadcastServiceManifest {

    @Inject
    public BroadcastServiceManifestImpl(@Named(BroadcastEvent.CATALOG) CatalogDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogEntry.NAME_FIELD));
    }
}
