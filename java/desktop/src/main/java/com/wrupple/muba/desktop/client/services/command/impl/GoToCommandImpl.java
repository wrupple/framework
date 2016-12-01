package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.GoToCommand;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class GoToCommandImpl implements GoToCommand {

	private JsArray<JsApplicationItem> userOutput;
	private StateTransition<DesktopPlace> callback;

	public GoToCommandImpl() {
	}

	@Override
	public void execute() {
			JsApplicationItem firstValue = userOutput.get(0);
		String[] activity= firstValue.getUri();
		DesktopPlace place = new DesktopPlace(activity);
		callback.setResultAndFinish(place);
	}

	@Override
	public void prepare(String command, JavaScriptObject activityContext,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionActivityContext processParameters,
			StateTransition<DesktopPlace> callback) {
		this.userOutput=processParameters.getUserOutput().cast();
		this.callback = (StateTransition<DesktopPlace>) callback;
		
	}

}
