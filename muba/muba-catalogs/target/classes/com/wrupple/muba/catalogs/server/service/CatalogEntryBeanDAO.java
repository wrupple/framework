package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface CatalogEntryBeanDAO extends
		GenericJavaObjectDAO<CatalogEntry>{

	public void setIncludePublicDomainInResults(boolean publicDomainReadAccess);
}
