package com.wrupple.muba.cms.domain;

import com.wrupple.vegetate.domain.CatalogEntry;


public interface WruppleDomainJavascript extends CatalogEntry{
	
	String CATALOG ="Javascript";
	
	String getValue();
	
	void setValue(String string);

}
