package com.wrupple.vegetate.domain;

import java.util.List;

public interface HasAccesablePropertyValues extends CatalogEntry{
	public Object getPropertyValue(String fieldId);

	public Object setPropertyValue(Object value, String fieldId);

	public Object getId();
	
	public Object getAsSerializable();

	public List<String> getProperties();
}