package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ParentServiceManifestImpl;
import com.wrupple.muba.event.domain.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessServiceManifest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

/**
 * Created by japi on 29/07/17.
 */
@Singleton
public class BusinessServiceManifestImpl extends ParentServiceManifestImpl implements BusinessServiceManifest{

    @Inject
    public BusinessServiceManifestImpl(@Named(ApplicationState.CATALOG) CatalogDescriptor contractDescriptorValue) {
        super(SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(SOLVE));
    }
}
