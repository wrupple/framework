package com.wrupple.muba.desktop.shared.services.factory.help;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface DictionaryConfigurableFactory<T> extends SolverConcensor {

    T get(JavaScriptObject configuration, ProcessContextServices services, EventBus bus, JsTransactionApplicationContext ctx);
	
	T get();

    void configure(T object, JavaScriptObject configuration, ProcessContextServices services, EventBus bus, JsTransactionApplicationContext ctx);
}
