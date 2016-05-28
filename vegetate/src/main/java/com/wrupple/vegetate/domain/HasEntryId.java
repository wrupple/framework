package com.wrupple.vegetate.domain;

public interface HasEntryId {
	String FIELD = "catalogEntryId";
	Object getCatalogEntryId();
	void setCatalogEntryId(String id);
}
