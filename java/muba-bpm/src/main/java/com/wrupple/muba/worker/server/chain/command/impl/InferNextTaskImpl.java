package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.WorkflowEngine;
import com.wrupple.muba.worker.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.worker.server.chain.command.InferNextTask;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InferNextTaskImpl implements InferNextTask {
    protected static final Logger log = LoggerFactory.getLogger(InferNextTaskImpl.class);

    private final WorkflowEngine outputHandler;

    @Inject
    public InferNextTaskImpl(WorkflowEngine outputHandler) {

        this.outputHandler = outputHandler;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();
         /*
            BusinessIntentImpl bookingRequest = new BusinessIntentImpl();
        bookingRequest.setHandle(item.getId());
        bookingRequest.setEntry(booking.getId());
        bookingRequest.setStateValue(null);

        */

        Application item = (Application) state.getHandleValue();

        Task nextTask = getNextWorkflowTask(item.getProcess(),item,state);
        if(nextTask==null){

            // PROCESAR SALIDA Y CAMBIAR DE PROCESO ( ReadNextPlace )
            //state.getProcessManager().getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) ;
            // output handler a clear example of when NOT to use events (sync same cotext=

            String command = item.getExit();
            if(command==null){
                if(item.getExplicitSuccessorValue()==null){
                    command= WorkflowEngine.NEXT_APPLICATION_ITEM;
                }else{
                    command = ExplicitOutputPlace.COMMAND;
                }

            }

           context.setName(command);

            log.info("firing workflow finished event to survey output Handlers");
            outputHandler.execute(context);
            state = context.getStateValue();
            Workflow newItem = (Workflow) state.getHandleValue();
            if(newItem.isClearOutput()){
                state.setEntryValue(null);
            }
            nextTask= newItem.getProcessValues().get(0);
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
