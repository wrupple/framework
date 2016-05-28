package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.HasLocale;



public interface WruppleLocalizedString extends CatalogEntry,HasLocale{
	String CATALOG= "LocalizedString";
	
	String getValue();

	String getName();
}
