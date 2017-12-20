package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;

import java.util.List;

public class SingletonListSelector<O> implements State<List<O>, O> {

	@Override
	public void start(List<O> parameter, StateTransition<O> onDone, EventBus bus) {
		assert parameter !=null;
		assert parameter.size()>0;
		onDone.setResultAndFinish(parameter.get(0));
	}

}
