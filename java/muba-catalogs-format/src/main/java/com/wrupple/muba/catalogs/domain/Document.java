package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public interface Document extends CatalogEntry {
	String getValue();
	
	void setValue(String string);
}
