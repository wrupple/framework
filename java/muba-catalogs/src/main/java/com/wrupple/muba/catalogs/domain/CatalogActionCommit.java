package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;

public interface CatalogActionCommit extends DataEvent,HasLiveContext {

    final String CATALOG = "CatalogActionCommit";

    CatalogActionRequest getRequestValue();

    void setRequestValue(CatalogActionRequest serviceContract);

}
