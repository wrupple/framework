package com.wrupple.vegetate.domain;

import java.util.List;


public interface FieldConstraint extends CatalogEntry {

	String CATALOG_ID = "FieldConstraint";
	String EVALUATING_VARIABLE="_value_";
	
	String getConstraint();

	List<String> getProperties();
}
