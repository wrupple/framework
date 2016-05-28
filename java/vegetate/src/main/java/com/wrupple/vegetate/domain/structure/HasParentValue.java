package com.wrupple.vegetate.domain.structure;

public interface HasParentValue<K, V> extends HasParent<K> {
	V getParentValue();
}
