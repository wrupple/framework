package com.wrupple.muba.event.domain;

import java.io.Serializable;

public interface CatalogKey extends Serializable{
	//FIXME references to this should be replace with CatalogKeyServices.getCatalogKeyFieldId
	public static final String ID_FIELD = "id";
	public static final String NAME_FIELD = "name";
	public static final String FOREIGN_KEY = "Value";
	public static final String MULTIPLE_FOREIGN_KEY = "Values";
	


	String getCatalogType();

	Object getId();
	

}
