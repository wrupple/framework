package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface HasAccesablePropertyValues extends HasProperties,CatalogEntry{
	public Object getPropertyValue(String fieldId);

	public Object setPropertyValue(Object value, String fieldId);

	
	public Object getAsSerializable();

}