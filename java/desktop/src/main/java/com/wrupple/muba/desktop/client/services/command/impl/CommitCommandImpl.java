package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class CommitCommandImpl implements CommitCommand {

	private StateTransition<JsTransactionActivityContext> callback;
	private ProcessContextServices processContext;
	private JsTransactionActivityContext processParameters;

	@Override
	public void prepare(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionActivityContext processParameters,
			StateTransition<JsTransactionActivityContext> callback) {
		this.callback = callback;
		this.processContext=processContext;
		
		JavaScriptObject userOutput = processContext.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor().getValue();
		processParameters.setUserOutput(userOutput);
		this.processParameters=processParameters;
		this.callback = callback;
		if(GWTUtils.hasAttribute(commandProperties, CANCEL_CONTEXT_PROPERTY)){
			processParameters.setCanceled(true);
		}
	}

	@Override
	public void execute() {
		StateTransition<JsTransactionActivityContext> contextCallback = processContext.getNestedTaskPresenter().getUserInteractionTaskCallback();
		if(contextCallback==callback){
			//they are not usually the same... but they might be
			contextCallback.setResultAndFinish(processParameters);
		}else{
			callback.setResultAndFinish(processParameters);
			contextCallback.setResultAndFinish(processParameters);
		}
	}

}
