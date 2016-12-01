package com.wrupple.muba.bootstrap.domain.reserved;

import java.util.List;

public interface HasProperties {
	
	final String PROPERTIES_FIELD = "properties";
	
	List<String> getProperties();
	
	void setProperties(List<String> properties);

}
