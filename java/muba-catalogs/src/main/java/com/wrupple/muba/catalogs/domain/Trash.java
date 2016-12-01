package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;

public interface Trash extends ContentNode ,HasCatalogId,HasEntryId{
	
	final String CATALOG = "Trash";
	final String TRASH_FIELD = "trash";

	//FIXME APPLIED SORT
	String getCatalog();
	
	boolean isRestored();
}