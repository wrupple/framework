package com.wrupple.muba.bpm.client.activity.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.event.HandlesCatalogEvents;
import com.wrupple.muba.desktop.client.services.logic.TaskProcessor;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface HumanTaskProcessor<T extends JavaScriptObject,R> extends TaskProcessor<T>,
HandlesCatalogEvents,HasValueChangeHandlers<R> , IsWidget{
	/**
	 *configuration framework alters properties then calls this to
	 * apply alterations (just to notify)
	 * 
	 * in general this means a complete reset of the widget, but in some
	 * cases real-timish alteration of the UI may be an option
	 * @param properties 
	 * @param contextServices TODO
	 * @param eventBus TODO
	 * @param contextParamenters TODO
	 */
	void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionActivityContext contextParamenters);
}