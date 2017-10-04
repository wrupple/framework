package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.ServiceManifest;

public interface CatalogServiceManifest  extends ServiceManifest{
	final String SERVICE_NAME = CatalogActionRequest.CATALOG_FIELD;
}