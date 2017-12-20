package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractCommitUserTransactionImpl;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public class CommitEditTransaction extends AbstractCommitUserTransactionImpl {

	
	@Override
	public void start(JsTransactionApplicationContext context,
                      StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
		super.start(context, onDone, bus);
		//MOVED TO CommitSubmissionImpl
	}
}
