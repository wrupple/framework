package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface HasVanityId extends CatalogEntry{
	final String FIELD = "vanityId";

	String getVanityId();
}
