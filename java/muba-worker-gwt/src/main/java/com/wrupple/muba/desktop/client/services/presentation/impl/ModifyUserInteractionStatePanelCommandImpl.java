package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.client.services.presentation.ModifyUserInteractionStatePanelCommand;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.PanelTransformationConfig;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class ModifyUserInteractionStatePanelCommandImpl implements ModifyUserInteractionStatePanelCommand{
    HumanTaskWindow panel;
    HumanTaskProcessor<?,?> transaction;
	PanelTransformationConfig config;
	private ProcessContextServices contextServices;
	private EventBus eventBus;
	private JsTransactionApplicationContext contextParamenters;
	
	@Override
	public void prepare(String command, JavaScriptObject properties,
			EventBus eventBus, ProcessContextServices processContext,JsTransactionApplicationContext contextParameters,
			StateTransition<JsTransactionApplicationContext> callback) {
		this.panel = processContext.getNestedTaskPresenter().getTaskContent();
		this.transaction =  processContext.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor();
		config = properties.cast();
		this.contextServices=processContext;
		this.eventBus=eventBus;
		this.contextParamenters=contextParameters;
	}

	@Override
	public void execute() {
		if("panel".equals(config.getTarget())){
			Toolbar toolbar = panel.getToolbarById(config.getToolbarId());
			//TODO pass alterations to a CMS TOolbar?
			//TODO create and add a new Toolbar? remove or replace even
			toolbar.applyAlterations(config, contextServices, eventBus, contextParamenters);
		}else if("transaction".equals(config.getTarget())){
			transaction.applyAlterations(config, contextServices, eventBus, contextParamenters);
		}
		
	}

}
