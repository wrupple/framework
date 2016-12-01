package com.wrupple.muba.bpm.client.activity.process.state;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;

/**
 * 
 * Changes or creates data from an input and notifies when it is done
 * 
 * @author japi
 * 
 */
public interface State<I, O> {


	public interface ContextAware<I, O> extends State<I, O> {

		void setContext(ProcessContextServices context);

	}

	 void start(final I parameter, final StateTransition<O> onDone,EventBus bus);
}
