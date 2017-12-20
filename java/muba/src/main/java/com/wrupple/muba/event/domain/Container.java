package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.domain.reserved.HasTimestamp;

public interface Container extends HasStakeHolder, HasTimestamp, CatalogEntry {

    String CATALOG = "Container";

    Host getPeerValue();

    Person getStakeHolderValue();

}
