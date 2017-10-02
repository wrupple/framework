package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.ImplicitIntent;
import com.wrupple.muba.event.domain.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.IntentResolverServiceManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by japi on 29/07/17.
 */
@Singleton
public class IntentResolverServiceManifestImpl extends ServiceManifestImpl implements IntentResolverServiceManifest {

    @Inject
    public IntentResolverServiceManifestImpl(@Named(ImplicitIntent.CATALOG)CatalogDescriptor contractDescriptorValue) {
        super(IntentResolverServiceManifest.SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(
                ImplicitIntent.CATALOG_FIELD, ImplicitIntent.OUTOUT_CATALOG));
    }
}
