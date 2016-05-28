package com.wrupple.muba.cms.domain;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface Document extends CatalogEntry {
	String getValue();
	
	void setValue(String string);
}
