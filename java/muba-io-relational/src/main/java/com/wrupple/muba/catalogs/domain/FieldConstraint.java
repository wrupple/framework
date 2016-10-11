package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.HasProperties;


public interface FieldConstraint extends CatalogEntry,HasProperties {

	String CATALOG_ID = "FieldConstraint";
	String EVALUATING_VARIABLE="_value_";
	
	String getConstraint();

}
