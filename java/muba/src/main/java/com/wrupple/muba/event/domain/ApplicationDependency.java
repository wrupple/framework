package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasDiscrimniator;

public interface ApplicationDependency extends CatalogEntry, HasDiscrimniator {

    String CATALOG = "ApplicationDependency";

}
