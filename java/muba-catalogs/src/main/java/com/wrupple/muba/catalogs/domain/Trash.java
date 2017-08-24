package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogKey;

public interface Trash extends ContentNode ,HasCatalogKey{
	
	final String CATALOG = "Trash";
	final String TRASH_FIELD = "trash";

	//FIXME APPLIED SORT
	String getCatalog();
	
	boolean isRestored();
}
