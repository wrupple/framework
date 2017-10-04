package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.ServiceManifest;

public interface CatalogActionFilterManifest extends ServiceManifest{

    final String SERVICE_NAME = "pre_"+CatalogServiceManifest.SERVICE_NAME;
}
