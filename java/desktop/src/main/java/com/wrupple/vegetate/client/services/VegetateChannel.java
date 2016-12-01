package com.wrupple.vegetate.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;

public interface VegetateChannel<T extends JavaScriptObject,R> {

	void send(T object, StateTransition<R> callback);
	
	void flush() throws Exception;
	
	public String buildServiceUrl(T object);
	
	
	public void getServiceManifest(DataCallback<JsVegetateServiceManifest> dataCallback) ;
}
