package com.wrupple.muba.bpm.client.services;

import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.State.ContextAware;


public interface Process<I, O> extends ContextAware<I, O> ,Iterable<State<?,?>> {

    void addState(State<?, ?> state);

    void addAll(Process<?, ?> process);

    /**
	 * @return current user interaction (if any)
	 */
	HumanTask<?, ?> getCurrentTask();
	
	/**
	 * @return current state, that may or may not be a user interaction Task
	 */
	State<?,?> getCurrentState();

    ProcessContextServices getContext();

}
