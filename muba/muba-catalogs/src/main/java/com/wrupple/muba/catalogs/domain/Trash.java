package com.wrupple.muba.catalogs.domain;

import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;

public interface Trash extends ContentNode ,HasCatalogId,HasEntryId{
	
	final String CATALOG = "Trash";
	final String TRASH_FIELD = "trash";

	String getCatalogId();
	
	boolean isRestored();
}
