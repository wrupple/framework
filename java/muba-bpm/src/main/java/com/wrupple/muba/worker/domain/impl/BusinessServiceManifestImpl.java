package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.impl.ParentServiceManifestImpl;
import com.wrupple.muba.worker.domain.Intent;
import com.wrupple.muba.worker.domain.BusinessServiceManifest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by japi on 29/07/17.
 */
@Singleton
public class BusinessServiceManifestImpl extends ParentServiceManifestImpl implements BusinessServiceManifest {

    @Inject
    public BusinessServiceManifestImpl(@Named(Intent.Intent_CATALOG) CatalogDescriptor contractDescriptorValue) {
        super(SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(SOLVE));
    }
}
