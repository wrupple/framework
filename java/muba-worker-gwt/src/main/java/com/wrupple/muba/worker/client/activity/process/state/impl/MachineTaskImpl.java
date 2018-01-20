package com.wrupple.muba.worker.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.state.MachineTask;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;

public class MachineTaskImpl implements MachineTask {


    private JsProcessTaskDescriptor activityDescriptor;
    private ProcessContextServices context;

    @Inject
    public MachineTaskImpl() {
        super();
    }

    @Override
    public void setTaskDescriptor(JsProcessTaskDescriptor activityDescriptor) {
        this.activityDescriptor = activityDescriptor;
    }

    @Override
    public void setContext(ProcessContextServices context) {
        this.context = context;
    }

    @Override
    public void start(final JsTransactionApplicationContext parameter,
                      final StateTransition<JsTransactionApplicationContext> onDone, EventBus bus) {
        parameter.setCurrentTaskIndex(parameter.getCurrentTaskIndex() + 1);
        parameter.setTaskDescriptor(activityDescriptor);
        String command = activityDescriptor.getMachineTaskCommandName();
        JavaScriptObject commandProperties = activityDescriptor.getPropertiesObject();
        EventBus eventBus = context.getEventBus();

        //FIXME some commands don't call the callback ever, assert all commands do!!! create a timer? or something to resume?


        StateTransition<JsTransactionApplicationContext> callback = new DataCallback<JsTransactionApplicationContext>() {

            @Override
            public void execute() {
                onDone.setResultAndFinish(parameter);
            }
        };
        context.getServiceBus().excecuteCommand(command, commandProperties, eventBus, context, parameter, callback);
    }

}
