package com.wrupple.muba.bpm.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.chain.command.InferNextTask;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InferNextTaskImpl implements InferNextTask {

    private final Provider<WorkflowFinishedEvent> eventProvider;

    @Inject
    public InferNextTaskImpl(Provider<WorkflowFinishedEvent> eventProvider) {
        this.eventProvider = eventProvider;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        BusinessContext context = (BusinessContext) ctx;
        BusinessEvent contractExplicitIntent = (BusinessEvent) context.getRuntimeContext().getServiceContract();
        ApplicationContext applicationState = context.getRuntimeContext().getConvertedResult();

         /*
            BusinessEventImpl bookingRequest = new BusinessEventImpl();
        bookingRequest.setHandle(item.getId());
        bookingRequest.setEntry(booking.getId());
        bookingRequest.setState(null);

        */

        Workflow item = (Workflow) applicationState.getHandleValue();
        List<ProcessTaskDescriptor> workflow = item.getProcessValues();

        ProcessTaskDescriptor nextTask ;
        int nextTaskIndex = applicationState.getTaskIndex()+1;
        if(workflow.size()<=nextTaskIndex){
            applicationState.setTaskIndex(nextTaskIndex);
            nextTask = workflow.get(applicationState.getTaskIndex());
        }else{

            // PROCESAR SALIDA Y CAMBIAR DE PROCESO ( ReadNextPlace )
            //state.getProcessManager().getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) ;

            WorkflowFinishedEvent event = eventProvider.get();

            event.setCatalog((String) applicationState.getTaskDescriptorValue().getCatalog());
            String command = applicationState.getApplicationValue().getExit();
            if(command==null){
                command=
            }
            event.setHandle(command);
            event.setState(applicationState);

            applicationState.getRuntimeContext().getEventBus().fireEvent(event);

            applicationState.setApplicationValue(event.getApplicationItemValue());
            nextTask= event.getTaskDescriptorValue();

        }

        applicationState.setTaskDescriptorValue(nextTask);
        applicationState.setTaskDescriptor(nextTask.getId());

        return CONTINUE_PROCESSING;
    }



}
