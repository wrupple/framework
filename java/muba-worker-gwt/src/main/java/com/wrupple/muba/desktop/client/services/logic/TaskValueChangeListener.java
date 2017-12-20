package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface TaskValueChangeListener extends ValueChangeHandler {

	void setContext(String catalog, JsTransactionApplicationContext parameter, ProcessContextServices context, JavaScriptObject properties, EventBus eventBus);

}
