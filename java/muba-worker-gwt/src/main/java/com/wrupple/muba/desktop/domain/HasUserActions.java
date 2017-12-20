package com.wrupple.muba.desktop.domain;

import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.domain.overlay.JsWruppleActivityAction;

public interface HasUserActions {
	void setAction(JsArray<JsWruppleActivityAction> actions);
	
}