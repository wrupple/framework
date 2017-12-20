package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.impl.ExcecuteCommandOnSelectionChange;
import com.wrupple.muba.desktop.client.services.presentation.BrowserSelectionModel;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.UserInteractionStateModifier;

import java.util.Set;

public class MultipleSelectionModel extends MultiSelectionModel<JsCatalogEntry> implements BrowserSelectionModel {

	public static final String NAME = UserInteractionStateModifier.MULTIPLE_SELECTION;
	
	@Inject
	public MultipleSelectionModel(
			CatalogEntryKeyProvider keyProvider) {
		super( keyProvider);
	}

	@Override
	public void setSelectionHandler(String command, JavaScriptObject selectionProperties, EventBus eventBus, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices) {
		GWT.log("[Selection Model] selectionHandler ="+command);
		if("void".equals(command)){
			command=null;
		} else if (command == null||"true".equals(command)) {
			command = CommitCommand.COMMAND;
		}
		if(command!=null){
			addSelectionChangeHandler(new ExcecuteCommandOnSelectionChange(selectionProperties, command, eventBus, contextParameters, contextServices));
		}
	}

	@Override
	public JsArray<JsCatalogEntry> getSelectedItems() {
		Set<JsCatalogEntry> set = super.getSelectedSet();
		JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
		for(JsCatalogEntry selected:set){
			regreso.push(selected);
		}
		return regreso;
	}

	
	
}
