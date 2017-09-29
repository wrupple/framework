package com.wrupple.muba.event.domain.reserved;

public interface HasParent<T> {
	final String FIELD ="parent";
	
	T getParent();

	
	T spawnChild();

	T getRootAncestor();
}
