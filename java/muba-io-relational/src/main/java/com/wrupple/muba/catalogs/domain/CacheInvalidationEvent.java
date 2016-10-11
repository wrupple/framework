package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

/**
 * 
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CacheInvalidationEvent {

	Long getDomain();

	String getCatalogId();

	String getAction();

	CatalogEntry getEntry();

	Object getEntryAsSerializable();
	

}