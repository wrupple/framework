package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.activity.process.state.PaymentSourceTokenApi;
import com.wrupple.muba.desktop.client.service.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class NoOpPaymentSourceTokenApi implements PaymentSourceTokenApi {

	@Override
	public void start(JsTransactionApplicationContext parameter, StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		onDone.setResultAndFinish(parameter);
	}

}
