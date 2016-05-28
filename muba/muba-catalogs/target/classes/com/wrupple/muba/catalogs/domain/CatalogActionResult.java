package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.vegetate.domain.VegetateResultSet;


public interface CatalogActionResult{
	
	/**
	 * @return the contents of the response
	 */
	List<? extends VegetateResultSet> getResponse();
	
	List<String> getWarnings();
	
	Long getResponseTimestamp();
	
}