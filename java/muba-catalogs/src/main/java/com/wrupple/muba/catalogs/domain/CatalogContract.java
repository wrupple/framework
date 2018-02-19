package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataContract;
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
public interface CatalogContract extends DataContract,HasLiveContext {

	String CATALOG = "CatalogContract";

    Object getEntryValue();

    List<Long> getExplicitlySuscriptedPeers();

    void setOldValues(List<CatalogEntry> oldValues);
}