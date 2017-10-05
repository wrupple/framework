package com.wrupple.muba.event.domain;

import java.util.List;

public interface ExplicitIntent extends Event {

	final String HANDLE_FIELD = "sentence";
    String CATALOG = "ExplicitIntent";


    /**
	 *
	 *
	 * worker session to recover
	 * @return (Application State Id, TaskId) if the input was an activity booking id , the output would be an activity tracking id
	 */
	Object getStateValue();

	void setStateValue(Object applicationState);

	<T > T getConvertedResult();

	/**
	 *
	 * @return alias to getHandle()
	 */
	List<String> getSentence();

	void setResult(Object result);

	void setError(Exception e);
}
