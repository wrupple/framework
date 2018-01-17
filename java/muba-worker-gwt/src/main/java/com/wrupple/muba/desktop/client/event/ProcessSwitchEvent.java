package com.wrupple.muba.desktop.client.event;

public class ProcessSwitchEvent extends DesktopProcessEvent {

	private final Process<?, ?> oldProcess;
	private final Process<?, ?> newProcess;

	public ProcessSwitchEvent(Process<?, ?> oldProcess, Process<?,?> newProcess) {
		this.oldProcess=oldProcess;
		this.newProcess = newProcess;
	}

	@Override
	protected void dispatch(DesktopProcessEventHandler handler) {
		handler.onProcessSwitch(this);
	}

	public Process<?, ?> getOldProcess() {
		return oldProcess;
	}

	public Process<?, ?> getNewProcess() {
		return newProcess;
	}


}
