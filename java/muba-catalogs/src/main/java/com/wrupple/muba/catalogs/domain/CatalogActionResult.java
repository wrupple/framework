package com.wrupple.muba.catalogs.domain;

import java.util.List;


public interface CatalogActionResult{
	
	/**
	 * @return the contents of the response
	 */
	List<? extends CatalogResultSet> getResponse();
	
	List<String> getWarnings();
	
	Long getResponseTimestamp();
	
}