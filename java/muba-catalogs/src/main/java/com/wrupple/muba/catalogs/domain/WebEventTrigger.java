package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface WebEventTrigger extends UserDefinedCatalogActionConstraint, HasDistinguishedName, HasProperties
{

	String CATALOG = "WebEventTrigger";
	

	/**
     * @return resolve on stakeHolder's authority?, basic auth, vegetate sign
     */
	int getAuthorizationMethod();
	


}
