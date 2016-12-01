package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.MachineTask;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public class MachineTaskImpl implements MachineTask {
	
	
	private JsProcessTaskDescriptor activityDescriptor;
	private ProcessContextServices context;

	@Inject
	public MachineTaskImpl() {
		super();
	}

	@Override
	public void setTaskDescriptor(JsProcessTaskDescriptor activityDescriptor) {
		this.activityDescriptor=activityDescriptor;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context=context;
	}

	@Override
	public void start(final JsTransactionActivityContext parameter,
			final StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		parameter.setCurrentTaskIndex(parameter.getCurrentTaskIndex()+1);
		parameter.setTaskDescriptor(activityDescriptor);
		String command = activityDescriptor.getMachineTaskCommandName();
		JavaScriptObject commandProperties=activityDescriptor.getPropertiesObject();
		EventBus eventBus=context.getEventBus();
		
		//FIXME some commands don't call the callback ever, assert all commands do!!! create a timer? or something to resume?
		
		
		StateTransition<JsTransactionActivityContext> callback=new DataCallback<JsTransactionActivityContext>() {

			@Override
			public void execute() {
				onDone.setResultAndFinish(parameter);
			}
		};
		context.getServiceBus().excecuteCommand(command, commandProperties, eventBus, context, parameter, callback);
	}

}
