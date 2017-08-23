package com.wrupple.muba.bootstrap.domain.reserved;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;

public interface TreeNode<K,V extends CatalogEntry> extends HasChildrenValues<K, V>, HasParentValue<K, V>,CatalogEntry {

}
