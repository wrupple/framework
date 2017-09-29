package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogIntent;
import com.wrupple.muba.event.domain.Intent;

public interface CatalogActionCommit extends CatalogIntent,HasLiveContext {

    final String CATALOG = "CatalogActionCommit";

    CatalogActionRequest getRequestValue();

    void setRequestValue(CatalogActionRequest serviceContract);

}
