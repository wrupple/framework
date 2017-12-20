package com.wrupple.muba.bpm.client.activity.process.impl;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ParallelProcess<I, O> implements Process<List<I>, List<O>> {

	class SequentialCallback extends DataCallback<O>{

		private int i;
		private List<I> parameter;
		private EventBus eventBus;
		public SequentialCallback(List<I> parameter, EventBus eventBus) {
			i = 0;
			this.eventBus=eventBus;
			this.parameter=parameter;
		}
		
		@Override
		public void execute() {
			i++;
			if(i < pending.size()){
				statep.get().start(parameter.get(i), pending.get(i).hook(this), eventBus);
			}
		}
	}
	
	class StateCallback extends DataCallback<O> {

		private final ArrayList<O> results;
		private final StateTransition<List<O>> globalCallback;
		private Object input;

		public StateCallback(Object input,StateTransition<List<O>> globalCallback,ArrayList<O> results) {
			super();
			this.input=input;
			assert results!=null;
			assert globalCallback !=null : "a callback must be provided for a parallel process to start";
			this.results=results;
			this.globalCallback=globalCallback;
		}

		@Override
		public String toString() {
			return "[" + input + "]";
		}

		@Override
		public void execute() {
			if(!allownull&&result==null){
				throw new IllegalArgumentException(input+": returned null, but no null results not allowed");
			}
			int index = pending.indexOf(this);
			results.set(index, result);
			resolved.add(this);
			if (resolved.size()==pending.size()) {
				globalCallback.setResultAndFinish(results);
			}else{
			}
		}

	}


	private final Provider<? extends State<I, O>> statep;
	private final boolean allownull,sequential;
	private List<StateCallback> pending;
	private List<StateCallback> resolved;
	private ProcessContextServices context;
	
	public ParallelProcess(final State<I, O> state, boolean allownull,boolean sequential) {
		this(new Provider<State<I,O>>(){
			@Override
			public State<I, O> get() {
				return state;
			}}, allownull,sequential);
	}

	public ParallelProcess(Provider<? extends State<I, O>> statep, boolean allownull,boolean sequential) {
		super();
		this.allownull=allownull;
		this.statep = statep;
		this.sequential=sequential;
	}

	@Override
	public void start(final List<I> parameter, final StateTransition<List<O>> onDone,
			final EventBus eventBus) {
		if(pending!=null || resolved!=null){
			throw new IllegalStateException("this paralel process has already been resolved");
		}
		
		ArrayList<O> results = new ArrayList<O>(parameter.size());
		pending = new ArrayList<StateCallback>(parameter.size());
		resolved = new ArrayList<StateCallback>(parameter.size());
		
		StateCallback callback;
		for (I input : parameter) {
			results.add(null);
			callback = new StateCallback(input,onDone, results);
			pending.add(callback);
		}
		if(sequential){
			if(parameter.isEmpty()){
				onDone.setResultAndFinish(Collections.EMPTY_LIST);
			}else{
				SequentialCallback scheduler = new SequentialCallback(parameter,eventBus);
				statep.get().start(parameter.get(scheduler.i), pending.get(scheduler.i).hook(scheduler), eventBus);
			}
		}else{
			Scheduler.get().scheduleIncremental(new RepeatingCommand() {
				private int index = 0;
				@Override
				public boolean execute() {
					statep.get().start(parameter.get(index), pending.get(index), eventBus);
					index++;
					return index < parameter.size();
				}
			});
		}
	}
	
	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}



	@Override
	public void addState(State<?, ?> state) {
		throw new UnsupportedOperationException("all states are expected to be of the same type");
	}

	@Override
	public ProcessContextServices getContext() {
		return context;
	}

	@Override
	public Iterator<State<?, ?>> iterator() {
		List list = Collections.singletonList(this.statep.get());
		return list.iterator();
	}

	@Override
	public void addAll(Process<?, ?> process) {
		for(State<?,?> s : process){
			addState(s);
		}
	}


	@Override
	public HumanTask<?, ?> getCurrentTask() {
		return null;
	}


	@Override
	public State<?, ?> getCurrentState() {
		return statep.get();
	}

}
