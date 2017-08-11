package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;

/**
 * an implicit intent has an input data type ( catalog ) and an optional output data type.
 */
public interface ImplicitIntent extends CatalogEntry,HasCatalogId {

    String CATALOG = "ImplicitIntent" ;
    String OUTOUT_CATALOG="outputCatalog";

    String getOutputCatalog();
	
}
