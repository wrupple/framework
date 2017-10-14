package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;

import java.util.List;

/**
 * Fired after an action is commited
 * acts kind of like a compressed version of vegetate's CatalogAction
 * request
 * 
 * @author japi
 *
 */
public interface CatalogEvent extends DataEvent,HasLiveContext {

	String CATALOG = "CatalogEvent";

    Object getEntryValue();

    List<Long> getExplicitlySuscriptedPeers();

    void setOldValues(List<CatalogEntry> oldValues);
}