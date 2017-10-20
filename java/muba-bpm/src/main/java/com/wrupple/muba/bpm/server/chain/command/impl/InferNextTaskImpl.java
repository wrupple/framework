package com.wrupple.muba.bpm.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.chain.WorkflowEngine;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.bpm.server.chain.command.InferNextTask;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InferNextTaskImpl implements InferNextTask {
    protected static final Logger log = LoggerFactory.getLogger(InferNextTaskImpl.class);

    private final Provider<WorkCompleteEvent> eventProvider;

    @Inject
    public InferNextTaskImpl(Provider<WorkCompleteEvent> eventProvider) {
        this.eventProvider = eventProvider;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        ApplicationContext context = (ApplicationContext) ctx;

         /*
            BusinessIntentImpl bookingRequest = new BusinessIntentImpl();
        bookingRequest.setHandle(item.getId());
        bookingRequest.setEntry(booking.getId());
        bookingRequest.setStateValue(null);

        */

        Workflow item = (Workflow) context.getStateValue().getHandleValue();

        Task nextTask = getNextWorkflowTask(item.getProcess(),item,context.getStateValue());
        if(nextTask==null){

            // PROCESAR SALIDA Y CAMBIAR DE PROCESO ( ReadNextPlace )
            //state.getProcessManager().getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) ;

            WorkCompleteEvent event = eventProvider.get();

            event.setCatalog((String) context.getStateValue().getTaskDescriptorValue().getCatalog());
            String command = item.getExit();
            if(command==null){
                if(item.getExplicitSuccessorValue()==null){
                    command= WorkflowEngine.NEXT_APPLICATION_ITEM;
                }else{
                    command = ExplicitOutputPlace.COMMAND;
                }

            }

            event.setName(command);
            event.setResult(context.getStateValue().getEntryValue());
            event.setStateValue(context.getStateValue());

            log.info("firing workflow finished event to survey output Handlers");
            context.getRuntimeContext().getEventBus().fireEvent(event,context.getRuntimeContext(),null);

            context.getStateValue().setHandleValue(event.getHandleValue());
            nextTask= event.getTaskDescriptorValue();

        }

        context.getStateValue().setTaskDescriptorValue(nextTask);
        context.getStateValue().setTaskDescriptor(nextTask.getId());

        return CONTINUE_PROCESSING;
    }

    private Task getNextWorkflowTask(List<Long> workflow, Workflow application, ApplicationState state) {
        int index = workflow.indexOf(state.getTaskDescriptorValue().getId());
        index++;
        if(index<workflow.size()){
            return application.getProcessValues().get(index);
        }else{
            return null;
        }

    }


}
