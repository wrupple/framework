package com.wrupple.muba.bpm.client.activity.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface Toolbar extends HumanTaskProcessor<JavaScriptObject,JavaScriptObject>,HasResizeHandlers {
	
	public void initialize(final JsTaskToolbarDescriptor toolbarDescriptor,
			JsProcessTaskDescriptor parameter,
			JsTransactionActivityContext contextParameters,final EventBus bus,
			final ProcessContextServices contextServices);
	
	public void setType(String s);
}
