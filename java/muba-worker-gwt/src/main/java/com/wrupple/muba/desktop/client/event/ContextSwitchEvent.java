package com.wrupple.muba.desktop.client.event;

import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.worker.client.activity.ActivityProcess;

public class ContextSwitchEvent extends DesktopProcessEvent {
	 private final ProcessContextServices context;
	 private final  ActivityProcess process;
	 
	public ContextSwitchEvent(ProcessContextServices context, ActivityProcess process) {
		super();
		this.context = context;
		this.process = process;
	}


	@Override
	protected void dispatch(DesktopProcessEventHandler handler) {
		handler.onContextSwitch(this);
	}


	public ProcessContextServices getContext() {
		return context;
	}


	public ActivityProcess getProcess() {
		return process;
	}

}
