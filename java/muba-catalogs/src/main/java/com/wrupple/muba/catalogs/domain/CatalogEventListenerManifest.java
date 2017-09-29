package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.ServiceManifest;

public interface CatalogEventListenerManifest extends ServiceManifest {

    final String SERVICE_NAME = "post_"+CatalogServiceManifest.SERVICE_NAME;

}
