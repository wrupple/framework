package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.Toolbar;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel.ToolbarDirection;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public  abstract class WruppleActivityToolbarBase extends UserInteractionWidgetImpl<JavaScriptObject,JavaScriptObject>
		implements Toolbar {


	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return super.addHandler(handler, ResizeEvent.getType());
	}


	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JavaScriptObject> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	protected int customSize;
	ToolbarDirection direction;
	
	protected String type;
	protected ProcessContextServices contextServices;
	protected EventBus eventBus;
	protected JsTransactionApplicationContext contextParameters;
	
	public WruppleActivityToolbarBase(ToolbarMap toolbarMap) {
		super(toolbarMap);
		customSize=-1;
	}

	@Override
	public void setType(String s){
		this.type=s;
	}
	
	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor,
                           JsProcessTaskDescriptor parameter,
                           JsTransactionApplicationContext contextParameters, EventBus bus,
                           ProcessContextServices contextServices) {
		this.contextServices=contextServices;
		this.eventBus=bus;
		this.contextParameters=contextParameters;
		
	}


	@Override
	protected void onAfterReconfigure(PanelTransformationConfig properties,
			ProcessContextServices contextServices, EventBus eventBus,
			JsTransactionApplicationContext contextParameters) {
		if(properties.getFireReset()){
			ResizeEvent.fire(this, 0, 0);
		}
		
	}

	@Override
	protected void onBeforeRecofigure(PanelTransformationConfig properties,
			ProcessContextServices contextServices, EventBus eventBus,
			JsTransactionApplicationContext contextParameters) {
		properties.setType(type);
	}
	
}
