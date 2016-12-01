package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
import com.wrupple.muba.bootstrap.domain.reserved.HasLocale;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface DistributiedLocalizedEntry extends CatalogEntry, HasLocale,HasCatalogId,HasEntryId ,HasProperties{

	
	String CATALOG = "DistributedCatalog";

	String getLocalizedFieldValue(String fieldId);
	
	Long getCatalog();
	
	Long getEntry();

	void setId(Object id);

}