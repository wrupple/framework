package com.wrupple.vegetate.domain;

import java.util.List;

public interface VegetateServiceManifest {
	final String ROOT_SERVICE_MANIFEST ="vegetate.manifest";
	
	//final String REPEAT_LAST_TOKEN="$";
	String getServiceName();
	
	String getServiceVersion();
	
	/**
	 * @return a unique string for this services version
	 */
	String getServiceId();

	/**
	 * Note: all services that wish to conform to  security shuold declare it's first token to be CatalogDescripto.DOMAIN_TOKEN
	 * 
	 * @return
	 */
	String[] getUrlPathParameters();

	String[] getChildServicePaths();
	
	List<? extends VegetateServiceManifest> getChildServiceManifests();

	CatalogDescriptor getContractDescriptor();
	
	Object createExcecutionContext(Object requestContext,String[] tokenValues,String serializedContext) throws Exception;

}
