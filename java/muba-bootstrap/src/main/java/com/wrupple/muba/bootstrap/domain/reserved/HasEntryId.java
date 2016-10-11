package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasEntryId {
	String ENTRY_ID_FIELD = "entry";
	Object getEntry();
	void setEntry(Object id);
}
