package com.wrupple.muba.bootstrap.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildrenValues;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasParentValue;

public interface ServiceManifest extends HasDistinguishedName,  HasChildrenValues<Long, ServiceManifest>,HasParentValue<Long,ServiceManifest>,CatalogEntry,HasCatalogId {
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
