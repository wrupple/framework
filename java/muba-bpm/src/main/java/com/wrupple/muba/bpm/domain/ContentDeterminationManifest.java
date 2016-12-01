package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.ServiceManifest;

public interface ContentDeterminationManifest extends ServiceManifest {

	final String SERVICE_NAME = "ai";
	
	final String FINAL_VALUE = "value";
	
	final String INITIAL_VALUE = "initialValue";
	
	String buildServiceRequestUri(char tokenSeparator,long domain,String catalog,String entry,String field,String finalValue, String initialValue);
}
