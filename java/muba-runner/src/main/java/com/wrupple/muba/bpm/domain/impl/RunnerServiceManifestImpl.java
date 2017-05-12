package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.RunnerServiceManifest;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by japi on 10/05/17.
 */
public class RunnerServiceManifestImpl   extends ServiceManifestImpl implements RunnerServiceManifest {

    private static final long serialVersionUID = 5444635742533027016L;

    @Inject
    public RunnerServiceManifestImpl(@Named(ProcessTaskDescriptor.CATALOG) CatalogDescriptor descriptor) {
        super(SERVICE_NAME, "1.0", descriptor, Arrays.asList(new String[] {
                //the only service token defined points to the Id of the task to perform, all other tokens
                //are defined by the task's specific url tokens list
                CatalogKey.ID_FIELD
        }));
    }
}
