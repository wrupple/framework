package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ServiceManifestImpl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class CatalogEventListenerManifestImpl extends ServiceManifestImpl implements CatalogEventListenerManifest {
    @Inject
    public CatalogEventListenerManifestImpl(@Named(CatalogEvent.CATALOG) CatalogDescriptor catalogValue) {
        super(SERVICE_NAME, "1.0", catalogValue, Arrays.asList(CatalogEvent.NAME_FIELD));
    }
}
