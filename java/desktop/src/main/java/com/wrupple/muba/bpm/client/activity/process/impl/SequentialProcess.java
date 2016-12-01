package com.wrupple.muba.bpm.client.activity.process.impl;

import java.util.Iterator;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.process.state.impl.TakesValueState;
import com.wrupple.muba.desktop.client.event.ProcessExitEvent;
import com.wrupple.muba.desktop.shared.OrdinalIndexedSequence;
/**
 * Our asynchronous finite state machine specially designed to handle States
 * that may be resolved by human interaction
 * 
 * @author japi
 * 
 * @param <I>
 * @param <O>
 */
public class SequentialProcess<I, O> extends OrdinalIndexedSequence<State<?, ?>> implements Process<I, O> {

	public class ProccessPipeline extends DataCallback<Object> {

		private SequenceIterator iterator;
		private StateTransition<Object> processFinishedCallback;
		private EventBus eventBus;

		public ProccessPipeline(EventBus eventBus, SequenceIterator iterator, StateTransition<Object> processFinishedCallback) {
			this.iterator = iterator;
			this.processFinishedCallback = processFinishedCallback;
			this.eventBus = eventBus;
		}

		@Override
		public void execute() {
			if (iterator.hasNext()) {
				// this is not the last step

				ProccessPipeline pipe = new ProccessPipeline(eventBus, iterator, processFinishedCallback);
				next(iterator, result, pipe, eventBus);
			} else {
				// this is the last step
				processFinishedCallback.setResult(result);

				fireDoneEvent(result, eventBus);
				processFinishedCallback.execute();
			}
		}

	}

	protected ProcessContextServices context;
	private HumanTask<?, ?> currentTask;
	private State<?, ?> currentState;
	SequenceIterator iterator;

	private void next(Iterator<State<?, ?>> iterator, Object parameter, ProccessPipeline pipeline, EventBus eventBus) {
		State<I, Object> next = (State<I, Object>) iterator.next();
		currentState = next;
		if (next instanceof ContextAware) {
			((ContextAware<?, ?>) next).setContext(context);
		}
		if (next instanceof HumanTask) {
			HumanTask<?, ?> task = (HumanTask<?, ?>) next;
			this.currentTask = task;
			IsWidget asWidget = task.asWidget();
			context.getNestedTaskPresenter().setWidget(asWidget);
		}
		GWT.log("[Sequential Process] start "+next.getClass().getSimpleName());
		next.start((I) parameter, pipeline, eventBus);
	}

	private void fireDoneEvent(Object result, EventBus eventBus) {
		eventBus.fireEvent(new ProcessExitEvent(this, result));
	}

	public void addState(State<?, ?> state) {
		assert state != null : "adding null states to a process is not supported";
		add(state);
	}

	public ProcessContextServices getContext() {
		return context;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

	public static <I, O> SequentialProcess<I, O> wrap(State<I, O> view) {
		SequentialProcess<I, O> regreso = new SequentialProcess<I, O>();
		regreso.addState(view);
		return regreso;
	}

	public static <O> SequentialProcess<O, O> wrap(HasValue<O> wrapped, IsWidget exactSameObjectAsWidget, String localizedName) {
		TakesValueState<O> singleState = new TakesValueState<O>(wrapped, exactSameObjectAsWidget);
		return wrap(singleState);
	}

	@Override
	public void start(I parameter, StateTransition<O> onDone, EventBus bus) {
		iterator = iterator();
		next(iterator, parameter, new ProccessPipeline(bus, iterator, (StateTransition<Object>) onDone), bus);
	}

	@Override
	public void addAll(Process<?, ?> process) {
		for (State<?, ?> s : process) {
			add(s);
		}
	}

	@Override
	public HumanTask<?, ?> getCurrentTask() {
		return currentTask;
	}

	@Override
	public State<?, ?> getCurrentState() {
		return currentState;
	}
}
