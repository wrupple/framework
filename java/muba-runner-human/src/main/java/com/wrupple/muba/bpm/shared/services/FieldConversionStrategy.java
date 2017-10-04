package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FilterCriteria;
import com.wrupple.muba.event.domain.FieldDescriptor;

import java.util.List;

public interface FieldConversionStrategy {

	Object convertToPersistentValue(Object fieldValue, FieldDescriptor fdescriptor);

	void setAsPersistentValue(Object value, FieldDescriptor field, CatalogEntry saveValueHere, Instrospection instrospection) throws ReflectiveOperationException;

	Object convertToPresentableValue(String id, CatalogEntry object, List<FilterCriteria> includeCriteria, Instrospection instrospection);

	//Object getUserReadableCollection(Object value, List<FilterCriteria> includeCriteria);
}
