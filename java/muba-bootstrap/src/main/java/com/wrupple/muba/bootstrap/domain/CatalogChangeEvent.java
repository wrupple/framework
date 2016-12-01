package com.wrupple.muba.bootstrap.domain;

/**
 * 
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CatalogChangeEvent extends CatalogIntent{

	String CATALOG = "CatalogChangeEvent";

	Object getEntryValue();

}