package com.wrupple.muba.catalogs.server.service;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;

/**
 * 
 * all implementations must be domain-aware. Catalog with id "X" may be
 * completely different in one domain or another
 * 
 * @author japi
 *
 */
public interface CatalogDescriptionCacheManager {

	/**
	 * @param catalogId
	 * @return cached descriptor for this Id, in current user's domain
	 */
	CatalogDescriptor get(String catalogId,CatalogExcecutionContext context);

	/**
	 * @param o
	 * @return cached descriptor for this Id, in current user's domain
	 */
	boolean removeKey(String catalogId,CatalogExcecutionContext context);

	/**
	 * @param catalogId
	 * @param regreso
	 * @return stored cached descriptor for this id in current user's domain
	 */
	CatalogDescriptor put(String catalogId, CatalogDescriptor regreso,CatalogExcecutionContext context);

}
