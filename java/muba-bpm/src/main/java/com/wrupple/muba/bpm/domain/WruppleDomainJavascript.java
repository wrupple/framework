package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;


public interface WruppleDomainJavascript extends CatalogEntry{
	
	String CATALOG ="Javascript";
	
	String getValue();
	
	void setValue(String string);

}