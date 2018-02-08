package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.ContextSwitch;
import com.wrupple.muba.desktop.domain.ContextSwitchManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;

public class ContextSwitchManifestImpl extends ServiceManifestImpl implements ContextSwitchManifest {

    @Inject
    public ContextSwitchManifestImpl(@Named(ContextSwitch.CATALOG) CatalogDescriptor catalogValue) {
        super(NAME, "1.0", catalogValue, Collections.EMPTY_LIST);
    }
}
