package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.*;

public interface DistributiedLocalizedEntry extends CatalogEntry, HasLocale,HasCatalogKey,HasProperties{

	
	String CATALOG = "DistributedCatalog";

	String getLocalizedFieldValue(String fieldId);
	
	Long getCatalog();
	
	Long getEntry();

	void setId(Object id);

}