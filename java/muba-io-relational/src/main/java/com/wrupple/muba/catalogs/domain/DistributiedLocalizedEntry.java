package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.HasLocale;
import com.wrupple.muba.bootstrap.domain.HasProperties;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;

public interface DistributiedLocalizedEntry extends CatalogEntry, HasLocale,HasCatalogId,HasEntryId ,HasProperties{

	
	String CATALOG = "DistributedCatalog";

	String getLocalizedFieldValue(String fieldId);
	
	Long getCatalog();
	
	Long getEntry();

}