package com.wrupple.muba.catalogs.server.service;

import java.lang.annotation.Annotation;
import java.util.List;

import com.wrupple.vegetate.domain.FieldConstraint;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;

public interface JSRAnnotationsDictionary {
	
	Annotation buildAnnotation(FieldConstraint constraint);

	List<String> getAvailableAnnotationNames();
	
	void initialize();

	CatalogKey buildCatalogKeyValidation(FieldDescriptor field);

	CatalogFieldValues buildNormalizationValidation(FieldDescriptor field);

}
