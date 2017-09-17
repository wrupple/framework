package com.wrupple.muba.bpm.server.chain.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.domain.WorkflowFinishedEvent;
import com.wrupple.muba.bpm.server.chain.command.GoToCommand;
import com.wrupple.muba.desktop.client.services.command.GoToCommand;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import org.apache.commons.chain.Context;

public class GoToCommandImpl implements GoToCommand {


	public GoToCommandImpl() {
	}

	@Override
	public boolean execute(Context ctx) {
		RuntimeContext context = (RuntimeContext) ctx;
		WorkflowFinishedEvent event = (WorkflowFinishedEvent) context.getServiceContract();
		ApplicationState state= (ApplicationState) event.getState();

		//Workflow currentItem = state.getApplicationValue();
        CatalogEntry output = state.getEntryValue();
        Workflow firstValue;
        if(output instanceof ServiceManifest){
            firstValue = (Workflow) output;
        }else{
            firstValue  = (Workflow) state.getUserSelectionValues().get(0);
        }

        state.setApplicationValue(firstValue);
		return CONTINUE_PROCESSING;
	}

	@Override
	public void prepare(String command, JavaScriptObject activityContext,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<DesktopPlace> callback) {
		this.userOutput=processParameters.getUserOutput().cast();
		this.callback = (StateTransition<DesktopPlace>) callback;
		
	}

}
