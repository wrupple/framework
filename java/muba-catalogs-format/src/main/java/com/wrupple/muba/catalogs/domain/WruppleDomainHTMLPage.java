package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface WruppleDomainHTMLPage extends CatalogEntry{
	String CATALOG="HTML";
	
	public String getValue();
	
	public void setValue(String string);
}
