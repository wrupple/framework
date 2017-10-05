package com.wrupple.muba.bpm.server.chain.command.impl;

import com.google.inject.Provider;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.chain.command.InferNextTask;
import com.wrupple.muba.catalogs.server.chain.command.impl.CatalogCreateTransactionImpl;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InferNextTaskImpl implements InferNextTask {
    protected static final Logger log = LoggerFactory.getLogger(InferNextTaskImpl.class);

    private final Provider<WorkflowFinishedIntent> eventProvider;

    @Inject
    public InferNextTaskImpl(Provider<WorkflowFinishedIntent> eventProvider) {
        this.eventProvider = eventProvider;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {

        ApplicationContext context = (ApplicationContext) ctx;
        BusinessIntent contractExplicitIntent = (BusinessIntent) context.getRuntimeContext().getServiceContract();
        ApplicationContext applicationState = context.getRuntimeContext().getConvertedResult();

         /*
            BusinessIntentImpl bookingRequest = new BusinessIntentImpl();
        bookingRequest.setHandle(item.getId());
        bookingRequest.setEntry(booking.getId());
        bookingRequest.setStateValue(null);

        */

        Workflow item = (Workflow) applicationState.getStateValue().getHandleValue();
        List<ProcessTaskDescriptor> workflow = item.getProcessValues();

        ProcessTaskDescriptor nextTask ;
        int nextTaskIndex = applicationState.getStateValue().getTaskIndex()+1;
        if(workflow.size()<=nextTaskIndex){
            applicationState.getStateValue().setTaskIndex(nextTaskIndex);
            nextTask = workflow.get(applicationState.getStateValue().getTaskIndex());
        }else{

            // PROCESAR SALIDA Y CAMBIAR DE PROCESO ( ReadNextPlace )
            //state.getProcessManager().getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) ;

            WorkflowFinishedIntent event = eventProvider.get();

            event.setCatalog((String) applicationState.getStateValue().getTaskDescriptorValue().getCatalog());
            String command = applicationState.getStateValue().getApplicationValue().getExit();
            if(command==null){
                command=null;
            }
            event.setResult(applicationState.getStateValue().getEntryValue());
            event.setStateValue(applicationState);

            log.info("firing workflow finished event to survey output Handlers");
            applicationState.getRuntimeContext().getEventBus().fireEvent(event,context.getRuntimeContext(),null);

            applicationState.getStateValue().setHandleValue(event.getApplicationItemValue());
            nextTask= event.getTaskDescriptorValue();

        }

        applicationState.getStateValue().setTaskDescriptorValue(nextTask);
        applicationState.getStateValue().setTaskDescriptor(nextTask.getId());

        return CONTINUE_PROCESSING;
    }



}
