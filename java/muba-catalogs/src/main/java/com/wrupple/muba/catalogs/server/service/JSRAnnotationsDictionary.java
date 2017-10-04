package com.wrupple.muba.catalogs.server.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.event.domain.annotations.CatalogKey;

public interface JSRAnnotationsDictionary {
	
	Annotation buildAnnotation(Constraint constraint);

	Constraint buildConstraint(Annotation annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

	List<String> getAvailableAnnotationNames();

	CatalogKey buildCatalogKeyValidation(FieldDescriptor field);

	CatalogFieldValues buildNormalizationValidation(FieldDescriptor field);

	void initialize();

}
