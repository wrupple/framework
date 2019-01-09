package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;

public class CatalogActionFilterManifestImpl extends ServiceManifestImpl implements CatalogActionFilterManifest {

    @Inject
    public CatalogActionFilterManifestImpl(@Named(CatalogActionFiltering.CATALOG) CatalogDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogActionFiltering.NAME_FIELD));
    }
}
