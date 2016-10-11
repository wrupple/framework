package com.wrupple.muba.bootstrap.domain;

import org.apache.commons.chain.Command;

public interface ServiceManifest {
	final String ROOT_SERVICE_MANIFEST ="vegetate.manifest";
	
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
	String[] getGrammar();


	ContractDescriptor getContractDescriptor();

	Command getContextParsingCommand();
	
	Command getContextProcessingCommand();
	
}
