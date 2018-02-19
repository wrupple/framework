package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CatalogIntentListenerManifestImpl extends ServiceManifestImpl implements CatalogIntentListenerManifest {
    @Inject
    public CatalogIntentListenerManifestImpl(@Named(CatalogContract.CATALOG) CatalogDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogContract.NAME_FIELD));
    }
}
