package com.wrupple.muba.bpm.client.services;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.domain.ProcessDescriptor;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.vegetate.client.services.StorageManager;

public interface TransactionalActivityAssembly extends
		State.ContextAware<ProcessDescriptor, ActivityProcess> {
	
	void setApplicationItem(ApplicationItem applicationItem);

	void loadProcess(String processId,
			StateTransition<List<JsProcessDescriptor>> transactionInfoCallback);
	
	void loadAndAssembleProcess(String processId,
			StateTransition<com.wrupple.muba.bpm.client.services.Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback);

	public void assembleActivityProcess(JsArray<JsProcessTaskDescriptor> processSteps,
			StateTransition<ActivityProcess> onDone);
	
	public ActivityProcess wrappProcess ( Process<?,?> regreso);
	
	public void assembleNativeProcess(Process<?,?> regreso,JsArray<JsProcessTaskDescriptor> processSteps) ;
	
	public StorageManager getSm();

}
