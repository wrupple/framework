package com.wrupple.muba.cms.domain;

import java.util.List;

public interface TaskToolbarDescriptor {
	
	String CATALOG = "ToolbarDescriptor";
	String TYPE_FIELD = "type";
	
	String getType();
	
	List<String> getProperties();
	
	Number getTask();

}
