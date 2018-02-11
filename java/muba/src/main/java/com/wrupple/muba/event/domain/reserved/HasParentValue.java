package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface HasParentValue<K, V> extends HasParent<K> {
	String VALUE_FIELD = FIELD + CatalogEntry.FOREIGN_KEY;

	V getParentValue();


	V getRootAncestor();

}
