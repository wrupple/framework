package com.wrupple.muba.desktop.client.event;

import com.wrupple.muba.bpm.client.services.Process;
public class ProcessExitEvent extends DesktopProcessEvent {

	private final com.wrupple.muba.bpm.client.services.Process<?,?> process;
	private final Object result;
	
	public ProcessExitEvent(Process<?, ?> process, Object result) {
		super();
		this.process = process;
		this.result = result;
	}

	@Override
	protected void dispatch(DesktopProcessEventHandler handler) {
		handler.onProcessDone(this);
	}

	public com.wrupple.muba.bpm.client.services.Process<?, ?> getProcess() {
		return process;
	}

	public Object getResult() {
		return result;
	}

}
