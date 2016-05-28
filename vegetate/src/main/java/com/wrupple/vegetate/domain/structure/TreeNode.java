package com.wrupple.vegetate.domain.structure;

import com.wrupple.vegetate.domain.CatalogEntry;

public interface TreeNode<K,V extends CatalogEntry> extends HasChildrenValues<K, V>, HasParent<K> {

	V getParentValue();
	
}
