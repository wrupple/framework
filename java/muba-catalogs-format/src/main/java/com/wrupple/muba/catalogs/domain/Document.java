package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface Document extends CatalogEntry {
	String getValue();
	
	void setValue(String string);
}
