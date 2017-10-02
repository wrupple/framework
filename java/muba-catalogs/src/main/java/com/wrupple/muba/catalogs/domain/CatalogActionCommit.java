package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEvent;

public interface CatalogActionCommit extends CatalogEvent,HasLiveContext {

    final String CATALOG = "CatalogActionCommit";

    CatalogActionRequest getRequestValue();

    void setRequestValue(CatalogActionRequest serviceContract);

}
