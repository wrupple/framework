package com.wrupple.muba.event.domain;

/**
 * an implicit intent has an input data type ( catalog ) and an optional output data type.
 */
public interface ImplicitIntent extends Event {

    String CATALOG = "ImplicitIntent" ;
    String OUTOUT_CATALOG="outputCatalog";

    //isnt this data need resolver by Event:HasCatalog??
    String getOutputCatalog();
	
}
