package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.*;

public interface DistributiedLocalizedEntry extends CatalogEntry, HasLocale,HasCatalogKey,HasProperties{

	
	String CATALOG = "DistributedCatalog";

	String getLocalizedFieldValue(String fieldId);
	
	Long getCatalog();
	
	Long getEntry();

	void setId(Object id);

}