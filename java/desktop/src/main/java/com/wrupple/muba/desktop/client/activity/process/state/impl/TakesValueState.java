package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.activity.widgets.DataInputView;
import com.wrupple.muba.desktop.domain.HasUserActions;
import com.wrupple.muba.desktop.domain.overlay.JsWruppleActivityAction;

public class TakesValueState<O> implements DataInputView<O>, ValueChangeHandler<O> {

	private HasValue<O> delegate;
	private IsWidget delegateAsWidget;
	private StateTransition<O> onDone;
	
	public TakesValueState(HasValue<O> view, IsWidget delegateAsWidget) {
		super();
		this.delegate = view;
		this.delegateAsWidget = delegateAsWidget;
		delegate.addValueChangeHandler(this);
	}

	@Override
	public void start(O parameter, StateTransition<O> onDone, EventBus bus) {
		delegate.setValue(parameter,false);
		this.onDone=onDone;
	}

	@Override
	public Widget asWidget() {
		return delegateAsWidget.asWidget();
	}

	@Override
	public void setValue(O value) {
		delegate.setValue(value);
	}

	@Override
	public O getValue() {
		return delegate.getValue();
	}

	@Override
	public void onValueChange(ValueChangeEvent<O> event) {
		onDone.setResultAndFinish(event.getValue());
	}

	@Override
	public void setAction(JsArray<JsWruppleActivityAction> actions) {
		if(delegate instanceof HasUserActions){
			((HasUserActions) delegate).setAction(actions);
		}		
	}


}
