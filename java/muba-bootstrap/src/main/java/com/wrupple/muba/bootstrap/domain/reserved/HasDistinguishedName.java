package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasDistinguishedName {
	final String FIELD = "distinguishedName";

	/**
	 * @return the DN of this entry
	 */
	String getDistinguishedName();
}
