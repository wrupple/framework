package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

import java.util.List;

/**
 * 
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CatalogEvent extends com.wrupple.muba.event.domain.CatalogEvent,HasLiveContext{

	String CATALOG = "CatalogEvent";

    Object getEntryValue();

    List<Long> getExplicitlySuscriptedPeers();

    void setOldValues(List<CatalogEntry> oldValues);
}