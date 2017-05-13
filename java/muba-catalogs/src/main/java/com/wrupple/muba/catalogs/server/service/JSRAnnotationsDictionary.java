package com.wrupple.muba.catalogs.server.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;

public interface JSRAnnotationsDictionary {
	
	Annotation buildAnnotation(Constraint constraint);

	Constraint buildConstraint(Annotation annotation) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;

	List<String> getAvailableAnnotationNames();

	com.wrupple.muba.catalogs.domain.annotations.CatalogKey buildCatalogKeyValidation(FieldDescriptor field);

	CatalogFieldValues buildNormalizationValidation(FieldDescriptor field);

	void initialize();

}
