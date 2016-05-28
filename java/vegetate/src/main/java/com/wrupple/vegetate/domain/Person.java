package com.wrupple.vegetate.domain;

public interface Person extends CatalogEntry,HasAccesablePropertyValues,HasLocale {

	String PERSON_ID_FIELD = "personId";

	String CATALOG = "Person";

	Long getId();

}
