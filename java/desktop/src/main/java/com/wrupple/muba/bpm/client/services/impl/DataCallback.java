package com.wrupple.muba.bpm.client.services.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;


public abstract class DataCallback<T> implements StateTransition<T> {
	protected T result;
	private ArrayList<StateTransition<T>> hooks;
	
	@Override
	public void setResult(T result) {
		this.result = result;
		if(hooks!=null){
			for(StateTransition<T>callback : hooks){
				callback.setResult(result);
				callback.execute();
			}
		}
	}
	
	@Override
	public StateTransition<T> hook(StateTransition<T> callback){
		if(this==callback){
			//the programer is just beeing an asshole
			
		}else{
			if(hooks==null){
				hooks = new ArrayList<StateTransition<T>>(3);
			}
			hooks.add(callback);
		}
		return this;
		
	}


	@Override
	public Collection<StateTransition<T>> getChildren() {
		return hooks;
	}

	@Override
	public void setResultAndFinish(T result) {
		setResult(result);
		execute();
	}
	
	public static <T> DataCallback<T> nullCallback(){
		return new DataCallback<T>(){

			@Override
			public void execute() {
				
			}
			
		};
	}

}
