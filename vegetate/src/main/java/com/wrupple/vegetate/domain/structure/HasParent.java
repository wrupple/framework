package com.wrupple.vegetate.domain.structure;

public interface HasParent<T> {
	final String FIELD ="parent";
	
	T getParent();

}
