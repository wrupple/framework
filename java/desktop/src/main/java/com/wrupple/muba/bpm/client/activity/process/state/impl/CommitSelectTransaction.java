package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractCommitUserTransactionImpl;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class CommitSelectTransaction extends AbstractCommitUserTransactionImpl {

	
	@Override
	public void start(JsTransactionActivityContext context,
			StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		super.start(context, onDone, bus);
		//no commiting required
		onDone.setResultAndFinish(context);
	}
}
