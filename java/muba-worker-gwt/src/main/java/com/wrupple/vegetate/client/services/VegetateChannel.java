package com.wrupple.vegetate.client.services;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.domain.overlay.JsVegetateServiceManifest;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;

public interface VegetateChannel<T extends JavaScriptObject,R> {

	void send(T object, StateTransition<R> callback);
	
	void flush() throws Exception;

    String buildServiceUrl(T object);


    void getServiceManifest(DataCallback<JsVegetateServiceManifest> dataCallback);
}
