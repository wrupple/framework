package com.wrupple.muba.bootstrap.domain.reserved;

public interface HasParent<T> {
	final String FIELD ="parent";
	
	T getParent();

}
