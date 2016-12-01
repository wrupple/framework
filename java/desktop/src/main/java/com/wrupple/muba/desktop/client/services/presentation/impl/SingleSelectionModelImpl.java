package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.impl.ExcecuteCommandOnSelectionChange;
import com.wrupple.muba.desktop.client.services.presentation.BrowserSelectionModel;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.muba.desktop.shared.services.UserInteractionStateModifier;

public class SingleSelectionModelImpl extends SingleSelectionModel<JsCatalogEntry> implements BrowserSelectionModel{

	public static final String NAME = UserInteractionStateModifier.SINGLE_SELECTION;
	
	@Inject
	public SingleSelectionModelImpl(
			CatalogEntryKeyProvider keyProvider) {
		super(keyProvider);
	}

	@Override
	public void setSelectionHandler(String command, JavaScriptObject selectionProperties, EventBus eventBus, JsTransactionActivityContext contextParameters,
			ProcessContextServices contextServices) {
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
		JsCatalogEntry selected = super.getSelectedObject();
		JsArray<JsCatalogEntry> regreso = JavaScriptObject.createArray().cast();
		regreso.push(selected);
		return regreso;
	}


}
