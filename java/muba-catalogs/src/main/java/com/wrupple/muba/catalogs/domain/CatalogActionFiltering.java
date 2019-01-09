package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;

public interface CatalogActionFiltering extends DataContract,HasLiveContext {

    final String CATALOG = "CatalogActionFiltering";

    CatalogActionRequest getRequestValue();

    void setRequestValue(CatalogActionRequest serviceContract);

}
