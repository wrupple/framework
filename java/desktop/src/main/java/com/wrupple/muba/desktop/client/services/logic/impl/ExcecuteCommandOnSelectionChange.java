package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class ExcecuteCommandOnSelectionChange implements Handler {

	private final EventBus eventBus;
	private final String command;
	private final JsTransactionApplicationContext contextParameters;
	private final ProcessContextServices contextServices;
	private JavaScriptObject selectionProperties;

	public ExcecuteCommandOnSelectionChange(JavaScriptObject selectionProperties,
			 String command, EventBus eventBus,
			JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices) {
		this.command = command;
		this.selectionProperties = selectionProperties;
		this.contextParameters = contextParameters;
		this.contextServices = contextServices;
		this.eventBus = eventBus;
	}

	// TODO use the same mechanism (or similar) to fire Value Change events
	// on Editors
	@Override
	public void onSelectionChange(SelectionChangeEvent e) {
		if (command!=null) {
			contextServices.getServiceBus().excecuteCommand(command,
					selectionProperties, eventBus, contextServices,
					contextParameters, null);
		} 
	}

}