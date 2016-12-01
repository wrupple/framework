package com.wrupple.muba.desktop.client.factory.help;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface DictionaryConfigurableFactory<T> extends UserAssistanceProvider{
	
	T get(JavaScriptObject configuration, ProcessContextServices services, EventBus bus, JsTransactionActivityContext ctx);
	
	T get();

	void configure(T object,JavaScriptObject configuration, ProcessContextServices services, EventBus bus, JsTransactionActivityContext ctx);
}
