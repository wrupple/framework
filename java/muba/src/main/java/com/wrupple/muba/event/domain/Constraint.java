package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasProperties;


public interface Constraint extends CatalogEntry,HasProperties,HasDistinguishedName {

	String CATALOG_ID = "Constraint";
	String EVALUATING_VARIABLE="_value_";
	//MESSAGE?
	

}
