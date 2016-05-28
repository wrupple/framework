package com.wrupple.vegetate.domain;

import java.io.Serializable;

public interface CatalogKey extends Serializable{
	//FIXME references to this should be replace with ImplicitJoinUtils.getCatalogKeyFieldId
	public static final String ID_FIELD = "id";
	public static final String NAME_FIELD = "name";
	public static final String FOREIGN_KEY = "Value";
	public static final String MULTIPLE_FOREIGN_KEY = "Values";

	String getCatalog();

	Object getId();
	

	// TODO this should be removed in favor of all ids in the client beeing
	// strings, and Object keys on the server which DAOs cast into apropiate
	// types, maybe even parsing them
	public String getIdAsString();

	public void setIdAsString(String id);

	

}
