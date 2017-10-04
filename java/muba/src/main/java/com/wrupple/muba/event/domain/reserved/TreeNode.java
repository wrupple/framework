package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.CatalogEntry;

public interface TreeNode<K,V extends CatalogEntry> extends HasChildrenValues<K, V>, HasParentValue<K, V>,CatalogEntry {

}
