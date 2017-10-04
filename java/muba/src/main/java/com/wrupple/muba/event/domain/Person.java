package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasLocale;
import com.wrupple.muba.event.domain.reserved.HasProperties;

public interface Person extends CatalogEntry,HasLocale,HasProperties {

	String PERSON_ID_FIELD = "personId";

	String CATALOG = "Person";

	Long getId();

}
