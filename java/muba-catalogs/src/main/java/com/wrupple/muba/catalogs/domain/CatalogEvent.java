package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogIntent;

import java.util.List;

/**
 * 
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CatalogEvent extends CatalogIntent,HasLiveContext{

	String CATALOG = "CatalogEvent";

    public final String DELETE_ACTION = "delete";

    public final String WRITE_ACTION = "write";

    public final String READ_ACTION = "read";

    public final String CREATE_ACTION = "new";

    Object getEntryValue();

    List<Long> getExplicitlySuscriptedPeers();

    void setOldValues(List<CatalogEntry> oldValues);
}