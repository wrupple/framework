package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasLocale;



public interface LocalizedString extends CatalogEntry,HasLocale{
	String CATALOG= "LocalizedString";
	
	String getValue();

}
