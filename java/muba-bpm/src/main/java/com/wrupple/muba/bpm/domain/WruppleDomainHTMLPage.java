package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public interface WruppleDomainHTMLPage extends CatalogEntry{
	String CATALOG="HTML";
	
	public String getValue();
	
	public void setValue(String string);
}
