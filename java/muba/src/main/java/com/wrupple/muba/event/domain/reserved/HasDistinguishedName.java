package com.wrupple.muba.event.domain.reserved;

public interface HasDistinguishedName {
	final String FIELD = "distinguishedName";

	/**
	 * @return the DN of this entry
	 */
	String getDistinguishedName();
}
