package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface HandlesDesktopInitializationEvents extends EventHandler {

	void onDesktopInitializationDone(DesktopInitializationDoneEvent event);

}
