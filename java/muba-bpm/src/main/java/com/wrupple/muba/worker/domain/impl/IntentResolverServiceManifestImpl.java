package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.worker.domain.IntentResolverServiceManifest;

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
    public IntentResolverServiceManifestImpl(@Named(Contract.Event_CATALOG)CatalogDescriptor contractDescriptorValue) {
        super(IntentResolverServiceManifest.SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(
                Contract.CATALOG_FIELD));
    }
}
