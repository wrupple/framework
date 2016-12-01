package com.wrupple.muba.desktop.client.event;


public class DesktopInitializationDoneEvent extends DesktopInitializationEvent {

	public DesktopInitializationDoneEvent() {
	}

	@Override
	protected void dispatch(HandlesDesktopInitializationEvents handler) {
		handler.onDesktopInitializationDone(this);
	}


}
