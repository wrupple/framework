package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface HasAccesablePropertyValues extends HasProperties,CatalogEntry{
	public Object getPropertyValue(String fieldId);

	public Object setPropertyValue(Object value, String fieldId);

	
	public Object getAsSerializable();

}