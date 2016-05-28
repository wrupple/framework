package com.wrupple.vegetate.domain.structure;

import java.util.List;

public interface HasChildrenValues<K, V> extends HasChildren<K> {
	List<V> getChildrenValues();

	void setChildrenValues(List<V> children);
}
