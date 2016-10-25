package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public interface HasDistinguishedName extends CatalogEntry{
	final String FIELD = "distinguishedName";

	/**
	 * @return the DN of this entry
	 */
	String getDistinguishedName();
}
