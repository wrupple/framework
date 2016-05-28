package com.wrupple.muba.catalogs.domain;

import java.util.List;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogTrigger;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.domain.HasStakeHolder;

public interface WebEventTrigger extends CatalogEntry, CatalogTrigger,HasEntryId,HasStakeHolder,HasVanityId
{

	String CATALOG = "WebEventTrigger";
	

	/**
	 * @return run on stakeHolder's authority?, basic auth, vegetate sign
	 */
	int getAuthorizationMethod();
	

	List<String> getProperties();

}
