package com.wrupple.vegetate.domain.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.RemoteBroadcast;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.vegetate.domain.RemoteServiceManifest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;

@Singleton
public class RemoteServiceManifestImpl extends ServiceManifestImpl implements RemoteServiceManifest {

    @Inject
    public RemoteServiceManifestImpl(@Named(RemoteBroadcast.CATALOG) CatalogDescriptor catalogValue) {
        super(NAME, catalogValue, Collections.EMPTY_LIST);
    }
}
