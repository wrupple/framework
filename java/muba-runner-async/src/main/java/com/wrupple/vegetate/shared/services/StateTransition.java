package com.wrupple.vegetate.shared.services;

import com.wrupple.muba.bootstrap.domain.reserved.HasChildren;

public interface StateTransition<T> extends  HasChildren<StateTransition<T>> {
	
	public void setResult(T previousStateOutput);
	
	StateTransition<T> hook(StateTransition<T> hooked);
	
	public void setResultAndFinish(T result);
	
	 void execute();
}
