package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.VegetateServiceManifest;

public interface CatalogServiceManifest  extends VegetateServiceManifest{
	final String SERVICE_NAME = CatalogActionRequest.CATALOG_ID_PARAMETER;
	CatalogDescriptor getContractDescriptor();
	
	String buildServiceRequestUri(char tokenSeparator,CatalogActionRequest request);

}
