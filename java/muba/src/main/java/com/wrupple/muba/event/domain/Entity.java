package com.wrupple.muba.event.domain;

public interface Entity extends CatalogKey {
	
	Object getImage();

	void setName(String name);

	/**
	 * @return the common name used to refer to this entity
	 */
	String getName();

}
