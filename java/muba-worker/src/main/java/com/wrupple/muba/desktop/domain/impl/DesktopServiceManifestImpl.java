package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.DesktopServiceManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class DesktopServiceManifestImpl extends ServiceManifestImpl implements DesktopServiceManifest {

    //as used by workerRequestInterpretImpl

    public DesktopServiceManifestImpl(@Named(ContainerRequest) CatalogDescriptor catalogValue) {
        super(NAME, catalogValue, Arrays.asList(CatalogEntry.NAME_FIELD));
    }
}
