package com.wrupple.muba.desktop.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface VegetateEventHandler extends EventHandler {

	void onNewVegetateRequest(NewVegetateRequestEvent e);

	void onRequestSuccessful(VegetateRequestSuccessEvent e);

	void onRequestFailed(VegetateRequestFailureEvent e);

}
