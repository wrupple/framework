package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface TaskToolbarDescriptor extends HasProperties,CatalogEntry {
	
	String CATALOG = "ToolbarDescriptor";
	String TYPE_FIELD = "type";
	
	String getType();
	
	
	Number getTask();

}
