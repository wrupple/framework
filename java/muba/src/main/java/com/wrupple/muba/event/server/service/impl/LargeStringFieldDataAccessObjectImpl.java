package com.wrupple.muba.event.server.service.impl;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;

@Singleton
public class LargeStringFieldDataAccessObjectImpl implements LargeStringFieldDataAccessObject {

	@Override
	public Type getLargeStringClass() {
		return String.class;
	}

	@Override
	public String getStringValue(Object fieldData) {
		return (String) fieldData;
	}

	@Override
	public Object processRawLongString(String s) {
		return s;
	}

}
