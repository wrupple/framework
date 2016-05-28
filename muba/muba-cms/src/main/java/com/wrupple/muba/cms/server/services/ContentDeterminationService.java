package com.wrupple.muba.cms.server.services;

import com.wrupple.vegetate.domain.VegetateServiceManifest;

public interface ContentDeterminationService extends VegetateServiceManifest {

	final String SERVICE_NAME = "bi";
	
	final String FINAL_VALUE = "value";
	
	final String INITIAL_VALUE = "initialValue";
	
	String buildServiceRequestUri(char tokenSeparator,long domain,String catalog,String entry,String field,String finalValue, String initialValue);
}
