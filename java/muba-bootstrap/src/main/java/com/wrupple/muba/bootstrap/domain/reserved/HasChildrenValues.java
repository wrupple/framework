package com.wrupple.muba.bootstrap.domain.reserved;

import java.util.List;

public interface HasChildrenValues<K, V> extends HasChildren<K> {
	List<V> getChildrenValues();

	void setChildrenValues(List<V> children);
}
