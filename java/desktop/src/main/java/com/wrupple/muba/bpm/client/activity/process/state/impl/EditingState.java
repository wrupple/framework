package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractUserInteractionState;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.presentation.ToolbarAssemblyDelegate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class EditingState extends AbstractUserInteractionState {

	private final ContentManagementSystem cms;
	
	@Inject
	public EditingState(ContentManagementSystem cms,TransactionPanelMap transactionPanelMap,
			ToolbarAssemblyDelegate userInterfaceAssembler, TaskValueChangeListener valueChangeListener) {
		super(transactionPanelMap, userInterfaceAssembler, valueChangeListener);
		this.cms=cms;
	}

	@Override
	protected HumanTaskProcessor<?,?> buildUserInteractionInterface(String catalog, JavaScriptObject properties, JsTransactionActivityContext parameter,
			EventBus eventBus, ProcessContextServices ctx) {
		ContentManager<JsCatalogEntry> contentManager = cms.getContentManager(catalog);
		HumanTaskProcessor<JsCatalogEntry,?> transactionView = contentManager.getUpdateTransaction(parameter, properties, eventBus, context);
		return transactionView;
	}


	

}
