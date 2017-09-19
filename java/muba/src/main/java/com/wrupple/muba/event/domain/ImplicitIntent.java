package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;

/**
 * an implicit intent has an input data type ( catalog ) and an optional output data type.
 */
public interface ImplicitIntent extends Intent {

    String CATALOG = "ImplicitIntent" ;
    String OUTOUT_CATALOG="outputCatalog";

    String getOutputCatalog();
	
}
