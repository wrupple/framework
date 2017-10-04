package com.wrupple.muba.catalogs.domain;

public interface HasLiveContext {


    CatalogActionContext getLiveContext();

    void setLiveContext(CatalogActionContext context);
}
