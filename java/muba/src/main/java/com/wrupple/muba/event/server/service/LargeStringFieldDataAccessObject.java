package com.wrupple.muba.event.server.service;

import java.lang.reflect.Type;

public interface LargeStringFieldDataAccessObject {

	Type getLargeStringClass();

	String getStringValue(Object fieldData);
	
	Object processRawLongString(String s);

}
