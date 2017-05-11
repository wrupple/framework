package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasCommand;
import com.wrupple.muba.bootstrap.domain.reserved.HasDescription;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface WruppleActivityAction extends CatalogEntry,HasProperties,HasDescription ,HasCommand{
	
	String CATALOG = "WruppleActivityAction";
	

	
	
}
