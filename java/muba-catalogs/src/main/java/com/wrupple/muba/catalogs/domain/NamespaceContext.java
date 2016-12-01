package com.wrupple.muba.catalogs.domain;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.domain.CatalogException;

/**
 * @author japi
 *
 */
public interface NamespaceContext extends Context,CatalogNamespace {

	

	public void setId(long requestedDomain,CatalogActionContext context) throws CatalogException;

	boolean isMultitenant();

	public void setNamespace(CatalogActionContext context);

	public void unsetNamespace(CatalogActionContext context);
	

}
