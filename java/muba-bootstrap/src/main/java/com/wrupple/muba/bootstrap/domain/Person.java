package com.wrupple.muba.bootstrap.domain;

public interface Person extends CatalogEntry,HasLocale,HasProperties {

	String PERSON_ID_FIELD = "personId";

	String CATALOG = "Person";

	Long getId();

}
