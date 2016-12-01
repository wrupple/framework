package com.wrupple.muba.desktop.client.services.logic;

public interface SerializationService<T,R> {
	R deserialize(String string) throws Exception;

	String serialize(T object)throws Exception;
}
