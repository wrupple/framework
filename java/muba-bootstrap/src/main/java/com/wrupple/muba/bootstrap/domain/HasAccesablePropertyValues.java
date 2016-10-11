package com.wrupple.muba.bootstrap.domain;

public interface HasAccesablePropertyValues extends HasProperties,CatalogEntry{
	public Object getPropertyValue(String fieldId);

	public Object setPropertyValue(Object value, String fieldId);

	
	public Object getAsSerializable();

}