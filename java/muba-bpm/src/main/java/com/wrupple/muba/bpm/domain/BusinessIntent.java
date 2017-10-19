package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.ExplicitIntent;

public interface BusinessIntent extends ManagedObject ,DataEvent,ExplicitIntent {

	String BusinessIntent_CATALOG = "BusinessIntent";
	//Date getDue();
	
	ApplicationState getStateValue();

	void setStateValue(ApplicationState applicationState);

	Object getState();
}
