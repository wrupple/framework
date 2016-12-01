package com.wrupple.muba.desktop.client.services.command;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface CommandService extends ScheduledCommand{

	void prepare(String command, JavaScriptObject commandProperties, EventBus eventBus,
			ProcessContextServices processContext, JsTransactionActivityContext processParameters, StateTransition<JsTransactionActivityContext> callback);
	
}