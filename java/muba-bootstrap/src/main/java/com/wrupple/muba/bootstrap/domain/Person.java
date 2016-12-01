package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasLocale;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;

public interface Person extends CatalogEntry,HasLocale,HasProperties {

	String PERSON_ID_FIELD = "personId";

	String CATALOG = "Person";

	Long getId();

}
