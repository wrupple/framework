package com.wrupple.muba.bootstrap.domain;

import com.wrupple.muba.bootstrap.domain.reserved.HasCatalogId;

public interface ExplicitIntent extends HasCatalogId{

	/**
	 * @return the proccess explicitly selected to handle this intent
	 */
	Object getHandle();

	/**
	 *
	 *
	 * worker session to recover
	 * @return (Application State Id) if the input was an activity booking id , the output would be an activity tracking id
	 */
	Object getState();
}
