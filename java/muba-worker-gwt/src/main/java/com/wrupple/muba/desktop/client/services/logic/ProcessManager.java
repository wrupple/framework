package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.ActivityProcess;
import com.wrupple.muba.worker.server.service.StateTransition;

public interface ProcessManager {

	<I, O> void processSwitch(com.wrupple.muba.bpm.client.services.Process<I, O> process, String localizedName, I input, StateTransition<O> callback, ProcessContextServices context);

	void contextSwitch(ActivityProcess activityState, JsApplicationItem applicationItem, AcceptsOneWidget container, EventBus bus);

	void getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback);

	void setCurrentProcess(String id);
	
	void setCurrentTask(String id);
}