package com.wrupple.muba.bpm.shared.services;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.FilterCriteria;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy;

import java.util.List;

public interface FieldConversionStrategy {

	Object convertToPersistentValue(Object fieldValue, FieldDescriptor fdescriptor);

	void setAsPersistentValue(Object value, FieldDescriptor field, CatalogEntry saveValueHere, FieldAccessStrategy.Session session) throws ReflectiveOperationException;

	Object convertToPresentableValue(String id, CatalogEntry object, List<FilterCriteria> includeCriteria, FieldAccessStrategy.Session session);

	//Object getUserReadableCollection(Object value, List<FilterCriteria> includeCriteria);
}
