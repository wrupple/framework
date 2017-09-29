package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;

public class CatalogActionFilterManifestImpl extends ServiceManifestImpl implements CatalogActionFilterManifest {

    @Inject
    public CatalogActionFilterManifestImpl(@Named(CatalogActionCommit.CATALOG) ContractDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogActionCommit.NAME_FIELD));
    }
}
