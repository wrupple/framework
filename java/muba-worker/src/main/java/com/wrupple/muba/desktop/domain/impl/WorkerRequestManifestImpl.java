package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class WorkerRequestManifestImpl extends ServiceManifestImpl implements WorkerRequestManifest {

    @Inject
    //as used by workerRequestInterpretImpl
    public WorkerRequestManifestImpl(@Named(WorkerContract.CATALOG) CatalogDescriptor catalogValue) {
        super(NAME, catalogValue, Arrays.asList(CatalogEntry.NAME_FIELD));
    }
}
