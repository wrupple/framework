package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasParentValue<K, V> extends HasParent<K> {
	V getParentValue();
}
