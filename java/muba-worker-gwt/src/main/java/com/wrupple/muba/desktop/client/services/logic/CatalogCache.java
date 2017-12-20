package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;

public interface CatalogCache {
	
	void setCatalog(String catalogid);
	String getCatalog();

	JsCatalogEntry read(String entryId);

	void put(String id, JsCatalogEntry entry);

	JsArray<JsCatalogEntry> read(int start, int length);

	void put(int start, JsArray<JsCatalogEntry> result);
	
	void remove(String id);
	
	int length();

	/**
	 * removes all entries from cache
	 */
	void invalidate();

	void forceAppend(JsCatalogKey createdEntry);

	void setComplete(boolean b);

	boolean isComplete();

	void setLastEntryCursor(String cursor);
	String getLastEntryCursor();
}
