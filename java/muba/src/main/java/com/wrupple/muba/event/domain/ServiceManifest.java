package com.wrupple.muba.event.domain;

import java.util.List;

import com.wrupple.muba.event.domain.reserved.*;

public interface ServiceManifest extends Service,ImplicitIntent,TreeNode<Long,ServiceManifest> {
	final String ROOT_SERVICE_MANIFEST ="vegetate.manifest";
	final String CATALOG = "ServiceManifest";
	
	
	String getVersionDistinguishedName();
	
	/**
	 * @return a unique string for this services version
	 */
	String getServiceId();
	
	public List<String> getChildrenPaths();

	ContractDescriptor getCatalogValue();

    void setParentValue(ServiceManifest serviceManifest);
}
