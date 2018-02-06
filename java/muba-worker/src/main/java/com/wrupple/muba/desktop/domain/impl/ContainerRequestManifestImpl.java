package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContainerRequest;
import com.wrupple.muba.desktop.domain.ContainerRequestManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ContainerRequestManifestImpl extends ServiceManifestImpl implements ContainerRequestManifest {

    //as used by workerRequestInterpretImpl
    public ContainerRequestManifestImpl(@Named(ContainerRequest.CATALOG) CatalogDescriptor catalogValue) {
        super(NAME, catalogValue, Arrays.asList(CatalogEntry.NAME_FIELD));
    }
}
