package com.wrupple.muba.desktop.client.services.logic;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONValue;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;

public interface FieldConversionStrategy {
	Object convertToPersistentDatabaseValue(Object fieldValue, FieldDescriptor fdescriptor);

	void convertToPersistentDatabaseValue(Object value,FieldDescriptor field,JavaScriptObject jso);

	Object convertToUserReadableValue(JSONValue rawValue);
	
	Object convertToUserReadableValue(String id, JavaScriptObject object,
			List<FilterCriteria> includeCriteria);

	
	

}
