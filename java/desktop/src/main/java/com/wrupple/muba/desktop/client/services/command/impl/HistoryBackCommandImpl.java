package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.History;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.HistoryBackCommand;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class HistoryBackCommandImpl implements HistoryBackCommand {

	@Override
	public void prepare(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionActivityContext processParameters,
			StateTransition<JsTransactionActivityContext> callback) {

	}

	@Override
	public void execute() {
		History.back();
	}

}
