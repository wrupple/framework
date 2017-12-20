package com.wrupple.muba.desktop.client.factory.help;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.PropertyValueAvisor;

public interface UserAssistanceProvider {

	void adviceOnCurrentConfigurationState(JavaScriptObject currentState,JsArray<PropertyValueAvisor> advice);

	/**
	 * @param fieldId
	 * @param value
	 * @param violations TODO
	 */
	void validateValue(String fieldId, Object value, JsArrayString violations);
	
	
	//TODO mapped advisor should implement a diffrente sub-interface
	void setRuntimeParameters(String type,ProcessContextServices ctx);
	
	
}
