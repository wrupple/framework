package com.wrupple.muba.cms.domain;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface WruppleDomainHTMLPage extends CatalogEntry{
	String CATALOG="HTML";
	
	public String getValue();
	
	public void setValue(String string);
}
