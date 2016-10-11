package com.wrupple.muba.catalogs.server.service;

import java.lang.reflect.Type;

public interface LargeStringFieldDataAccessObject {

	Type getLargeStringClass();

	String getStringValue(Object fieldData);
	
	Object processRawLongString(String s);

}
