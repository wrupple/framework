package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.TakesValue;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

public interface ToolbarAssemblyDelegate {

    void assebleToolbars(HumanTaskWindow transactionView,
                         TakesValue<?> mainTransaction, JsProcessTaskDescriptor parameter, JavaScriptObject taskDescriptorProps, JsTransactionApplicationContext activityContext, EventBus bus,
                         ProcessContextServices context, JsApplicationItem currentPlace);
	
}
