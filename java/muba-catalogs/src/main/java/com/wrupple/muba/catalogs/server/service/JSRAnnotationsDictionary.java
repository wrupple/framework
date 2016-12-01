package com.wrupple.muba.catalogs.server.service;

import java.lang.annotation.Annotation;
import java.util.List;

import com.wrupple.muba.catalogs.domain.Constraint;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;

public interface JSRAnnotationsDictionary {
	
	Annotation buildAnnotation(Constraint constraint);

	List<String> getAvailableAnnotationNames();
	

	com.wrupple.muba.catalogs.domain.annotations.CatalogKey buildCatalogKeyValidation(FieldDescriptor field);

	CatalogFieldValues buildNormalizationValidation(FieldDescriptor field);

	void initialize();

}
