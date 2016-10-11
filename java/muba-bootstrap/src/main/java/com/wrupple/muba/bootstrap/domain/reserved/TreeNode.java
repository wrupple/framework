package com.wrupple.muba.bootstrap.domain.reserved;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;

public interface TreeNode<K,V extends CatalogEntry> extends HasChildrenValues<K, V>, HasParent<K> {

	V getParentValue();
	
}
