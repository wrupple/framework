package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DesktopProcessEventHandler extends EventHandler {

	void onProcessSwitch(ProcessSwitchEvent e);

	void onContextSwitch(ContextSwitchEvent e);

	void onProcessDone(ProcessExitEvent e);

}
