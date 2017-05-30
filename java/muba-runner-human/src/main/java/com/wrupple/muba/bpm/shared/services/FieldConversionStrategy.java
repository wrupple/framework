package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

import java.util.List;

public interface FieldConversionStrategy {
	Object convertToPersistentDatabaseValue(Object fieldValue, FieldDescriptor fdescriptor);

	void convertToPersistentDatabaseValue(Object value,FieldDescriptor field,CatalogEntry saveValueHere);

	
	Object convertToUserReadableValue(String id, CatalogEntry object,
			List<FilterCriteria> includeCriteria);

}
