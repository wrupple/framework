package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.WorkCompleteEvent;
import com.wrupple.muba.bpm.domain.WorkflowServiceManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

public class WorkflowServiceManifestImpl extends ServiceManifestImpl implements WorkflowServiceManifest {



    @Inject
    public WorkflowServiceManifestImpl(@Named(WorkCompleteEvent.CATALOG) CatalogDescriptor contractDescriptorValue) {
        super(SERVICE_NAME, "1.0", contractDescriptorValue, Arrays.asList(HasDistinguishedName.FIELD));
    }
}
