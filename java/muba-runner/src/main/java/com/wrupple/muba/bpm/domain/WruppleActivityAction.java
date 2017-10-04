package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasCommand;
import com.wrupple.muba.event.domain.reserved.HasDescription;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface WruppleActivityAction extends CatalogEntry,HasProperties,HasDescription ,HasCommand{
	
	String CATALOG = "WruppleActivityAction";
	

	
	
}
