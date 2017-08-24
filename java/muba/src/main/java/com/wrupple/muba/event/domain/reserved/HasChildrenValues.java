package com.wrupple.muba.event.domain.reserved;

import java.util.List;

public interface HasChildrenValues<K, V> extends HasChildren<K> {
	List<V> getChildrenValues();

	void setChildrenValues(List<V> children);
}
