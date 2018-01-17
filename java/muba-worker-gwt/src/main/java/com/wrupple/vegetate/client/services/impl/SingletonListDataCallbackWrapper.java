package com.wrupple.vegetate.client.services.impl;

import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.worker.client.services.impl.DataCallback;

import java.util.List;

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
