package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.WorkflowFinishedIntent;
import com.wrupple.muba.bpm.domain.WorkflowServiceManifest;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ServiceManifestImpl;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

public class WorkflowServiceManifestImpl extends ServiceManifestImpl implements WorkflowServiceManifest {



    @Inject
    public WorkflowServiceManifestImpl(@Named(WorkflowFinishedIntent.CATALOG) ContractDescriptor contractDescriptorValue) {
        super(SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(HasDistinguishedName.FIELD));
    }
}
