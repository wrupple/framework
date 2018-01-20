package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.LaunchWorkerManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

public class LaunchWorkerManifestImpl extends ServiceManifestImpl implements LaunchWorkerManifest {

    @Inject
    public LaunchWorkerManifestImpl(@Named(ContainerState.CATALOG) CatalogDescriptor contract) {
        super(LaunchWorkerManifest.NAME, "1.0", contract, Arrays.asList("urlTokens..."));
    }
}
