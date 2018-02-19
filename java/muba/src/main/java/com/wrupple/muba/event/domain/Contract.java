package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;

public interface Contract extends CatalogEntry, HasCatalogId{
    String Event_CATALOG = "Contract" ;

}
