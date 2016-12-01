package com.wrupple.vegetate.client.services.impl;

import java.util.List;

import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;

public class SingletonListDataCallbackWrapper<T> extends DataCallback<List<T>> {

	StateTransition<T> wrapped;
	
	
	public SingletonListDataCallbackWrapper(StateTransition<T> wrapped) {
		super();
		this.wrapped = wrapped;
	}



	@Override
	public void execute() {
		if(result==null||result.isEmpty()){
			wrapped.setResultAndFinish(null);
		}else{
			wrapped.setResultAndFinish(result.get(0));
		}
	}

}
