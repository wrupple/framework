package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasCatalogId;

public interface ExplicitIntent extends HasCatalogId{

	final String HANDLE_FIELD = "handle";

	/**
	 * @return the proccess explicitly selected to handle this intent
	 */
	Object getHandle();

	/**
	 *
	 *
	 * worker session to recover
	 * @return (Application State Id, TaskId) if the input was an activity booking id , the output would be an activity tracking id
	 */
	Object getState();

	void setHandle(Object command);

	void setState(Object applicationState);
}
