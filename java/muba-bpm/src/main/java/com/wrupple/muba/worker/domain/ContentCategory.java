package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface ContentCategory extends CatalogEntry{

	String getForeignKeyField();
	
}
