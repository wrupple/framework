package com.wrupple.muba.catalogs.domain;

import org.apache.commons.chain.Context;

/**
 * @author japi
 *
 */
public interface NamespaceContext extends Context,CatalogNamespace {

	

	public void setId(long requestedDomain,CatalogActionContext context) throws Exception;

	public void switchToUserDomain(CatalogActionContext context)throws Exception;
	
	boolean isMultitenant();

	public void setNamespace(CatalogActionContext context);

	public void unsetNamespace(CatalogActionContext context);
	

}
