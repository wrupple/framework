package com.wrupple.muba.desktop.client.activity.widgets.impl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.activity.widgets.Prompt;
import com.wrupple.muba.worker.server.service.StateTransition;

public class SimplePromt<I> extends ResizeComposite implements Prompt<I>{

	private static SimplePromtUiBinder uiBinder = GWT.create(SimplePromtUiBinder.class);

	interface SimplePromtUiBinder extends UiBinder<Widget, SimplePromt<?>> {
	}
	
	@UiField(provided=true)
	Widget contained;
	private StateTransition<Void> callback;
	private TakesValue<I> asTakesValue;
	

	public SimplePromt(IsWidget w, TakesValue<I> asTakesValue) {
		contained = w.asWidget();
		this.asTakesValue=asTakesValue;
		initWidget(uiBinder.createAndBindUi(this));
	}


	@Override
	public void start(I parameter, StateTransition<Void> onDone, EventBus bus) {
		asTakesValue.setValue(parameter);
		this.callback = onDone;
	}
	
	@UiHandler("ok")
	public void ok(ClickEvent e){
		callback.setResultAndFinish(null);
	}

}
