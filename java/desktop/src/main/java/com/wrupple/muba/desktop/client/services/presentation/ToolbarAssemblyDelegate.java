package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.TakesValue;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface ToolbarAssemblyDelegate {
	
	void assebleToolbars(ContentPanel transactionView,
			TakesValue<?> mainTransaction, JsProcessTaskDescriptor parameter,JavaScriptObject taskDescriptorProps, JsTransactionActivityContext activityContext, EventBus bus,
			ProcessContextServices context, JsApplicationItem currentPlace);
	
}
