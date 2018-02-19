package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;

public interface CatalogActionCommit extends DataContract,HasLiveContext {

    final String CATALOG = "CatalogActionCommit";

    CatalogActionRequest getRequestValue();

    void setRequestValue(CatalogActionRequest serviceContract);

}
