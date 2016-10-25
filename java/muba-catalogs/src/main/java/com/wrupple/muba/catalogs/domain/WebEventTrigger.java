package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.HasProperties;

public interface WebEventTrigger extends CatalogTrigger,HasDistinguishedName,HasProperties
{

	String CATALOG = "WebEventTrigger";
	

	/**
	 * @return run on stakeHolder's authority?, basic auth, vegetate sign
	 */
	int getAuthorizationMethod();
	


}
