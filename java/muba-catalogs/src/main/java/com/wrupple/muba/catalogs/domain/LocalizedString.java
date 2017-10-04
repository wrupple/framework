package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasLocale;



public interface LocalizedString extends CatalogEntry,HasLocale{
	String CATALOG= "LocalizedString";
	
	String getValue();

}
