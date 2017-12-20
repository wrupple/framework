package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;

public interface FilterCriteriaFieldDelegate {

	 JsArray<JsArrayString> getOperatorOptions(JsFieldDescriptor field);

}
