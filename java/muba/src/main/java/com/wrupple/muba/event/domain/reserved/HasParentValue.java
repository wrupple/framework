package com.wrupple.muba.event.domain.reserved;

public interface HasParentValue<K, V> extends HasParent<K> {
	V getParentValue();


	V getRootAncestor();

}
