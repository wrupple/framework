package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;

import java.util.List;

public interface ExplicitIntent extends Intent{

	final String HANDLE_FIELD = "sentence";
    String CATALOG = "ExplicitIntent";


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
	List<String> getSentence();

	void setResult(Object result);

	<R> R setError(Exception e);
}
