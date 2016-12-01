package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.SelectionModel;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface BrowserSelectionModel extends SelectionModel<JsCatalogEntry> {

	void setSelectionHandler(String command,JavaScriptObject selectionProperties, EventBus eventBus, JsTransactionActivityContext contextParameters, ProcessContextServices contextServices);

	JsArray<JsCatalogEntry> getSelectedItems();
	
}
