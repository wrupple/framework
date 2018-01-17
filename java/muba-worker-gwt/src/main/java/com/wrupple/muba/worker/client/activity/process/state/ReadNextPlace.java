package com.wrupple.muba.worker.client.activity.process.state;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

public interface ReadNextPlace
		extends
		State.ContextAware<JavaScriptObject, DesktopPlace> {


	void setApplicationItem(JsApplicationItem applicationItem);
	
}
