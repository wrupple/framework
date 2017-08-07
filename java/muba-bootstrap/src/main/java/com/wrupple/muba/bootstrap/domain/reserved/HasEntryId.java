package com.wrupple.muba.bootstrap.domain.reserved;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public interface HasEntryId {
	String ENTRY_ID_FIELD = "entry";

	Object getEntry();
	void setEntry(Object id);

	//Object getEntryValue();

}
