package com.wrupple.muba.desktop.shared;

import com.google.web.bindery.event.shared.EventBus;

public interface HasEventBus {

	void setEventBus(EventBus bus);

	EventBus getEventBus();
}
