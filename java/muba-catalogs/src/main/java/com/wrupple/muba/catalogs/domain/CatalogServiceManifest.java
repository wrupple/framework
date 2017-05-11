package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;

public interface CatalogServiceManifest  extends ServiceManifest{
	final String SERVICE_NAME = CatalogActionRequest.CATALOG_FIELD;
}