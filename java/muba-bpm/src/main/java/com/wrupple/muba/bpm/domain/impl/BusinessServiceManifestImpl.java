package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessServiceManifest;

import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

/**
 * Created by japi on 29/07/17.
 */
public class BusinessServiceManifestImpl extends ServiceManifestImpl implements BusinessServiceManifest{

    public BusinessServiceManifestImpl(@Named(ApplicationState.CATALOG) ContractDescriptor contractDescriptorValue) {
        super(SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(SOLVE));
    }
}
