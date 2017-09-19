package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;

public interface ExplicitIntent extends Intent{

	final String HANDLE_FIELD = "handle";


	/**
	 *
	 *
	 * worker session to recover
	 * @return (Application State Id, TaskId) if the input was an activity booking id , the output would be an activity tracking id
	 */
	Object getState();

	void setState(Object applicationState);

	<T > T getConvertedResult();

	/**
	 *
	 * @return alias to getHandle()
	 */
	String[] getSentence();

	void setResult(Object result);
}
