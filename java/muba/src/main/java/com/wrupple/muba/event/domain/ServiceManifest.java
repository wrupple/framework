package com.wrupple.muba.event.domain;

import java.util.List;

import com.wrupple.muba.event.domain.reserved.*;

public interface ServiceManifest extends HasProperties,HasStakeHolder,HasDistinguishedName,ImplicitIntent,HasCatalogId,TreeNode<Long,ServiceManifest> {
	final String ROOT_SERVICE_MANIFEST ="vegetate.manifest";
	final String CATALOG = "ServiceManifest";
	
	
	String getVersionDistinguishedName();
	
	/**
	 * @return a unique string for this services version
	 */
	String getServiceId();

	/**
	 * Note: all services that wish to conform to  security should declare it's first token to be CatalogEntry.DOMAIN_TOKEN
	 * 
	 * @return
	 */
	List<String> getGrammar();
	
	public List<String> getChildrenPaths();

	ContractDescriptor getCatalogValue();

    void setParentValue(ServiceManifest serviceManifest);
}
