package com.wrupple.muba.event.domain.reserved;

public interface HasParent<T> {
	String FIELD ="parent";
	
	T getParent();

}
