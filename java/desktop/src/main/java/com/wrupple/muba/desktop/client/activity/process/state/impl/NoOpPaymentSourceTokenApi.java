package com.wrupple.muba.desktop.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.activity.process.state.PaymentSourceTokenApi;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class NoOpPaymentSourceTokenApi implements PaymentSourceTokenApi {

	@Override
	public void start(JsTransactionActivityContext parameter, StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		onDone.setResultAndFinish(parameter);
	}

}
