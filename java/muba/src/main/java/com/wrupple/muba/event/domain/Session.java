package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.domain.reserved.HasTimestamp;

public interface Session extends HasStakeHolder,HasTimestamp,CatalogEntry{

    String CATALOG = "Session";

    Host getPeerValue();

    Person getStakeHolderValue();

}
