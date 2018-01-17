package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.worker.client.activity.process.state.State;

public class ArbitraryActivityExit implements State<Object, DesktopPlace> {

	DesktopPlace exit;
	
	
	public ArbitraryActivityExit(DesktopPlace exit) {
		super();
		this.exit = exit;
	}



	@Override
	public void start(Object parameter, StateTransition<DesktopPlace> onDone,EventBus eventBus) {
		onDone.setResultAndFinish(exit);
	}



}
