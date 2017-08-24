package com.wrupple.muba.event.domain.reserved;

public interface HasDescription {
	final String DESCRIPTION_FIELD="description";
	
	String getDescription();
	
	void setDescription(String desc);
}
