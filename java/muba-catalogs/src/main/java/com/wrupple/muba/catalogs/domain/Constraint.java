package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;


public interface Constraint extends CatalogEntry,HasProperties,HasDistinguishedName {

	String CATALOG_ID = "Constraint";
	String EVALUATING_VARIABLE="_value_";
	//MESSAGE?
	

}
