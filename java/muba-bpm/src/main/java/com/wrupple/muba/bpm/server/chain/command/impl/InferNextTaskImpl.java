package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.bpm.domain.BusinessEvent;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.InferNextTask;
import com.wrupple.muba.bpm.server.domain.BusinessContext;
import org.apache.commons.chain.Context;

import java.util.List;

public class InferNextTaskImpl implements InferNextTask {
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

        ApplicationItem item = (ApplicationItem) applicationState.getHandleValue();
        List<ProcessTaskDescriptor> workflow = item.getProcessValues();


        //state.getProcessManager().getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) ;
        state.getServiceBus().parseOutput();

        ProcessTaskDescriptor nextTask = ;
        if(nextTask==null){
            //FIXME PROCESAR SALIDA Y CAMBIAR DE PROCESO ( ReadNextPlace )

        }else{
            applicationState.setTaskDescriptorValue(nextTask);
            applicationState.setTaskDescriptor(nextTask.getId());
        }


        return CONTINUE_PROCESSING;
    }

    @Override
    public void parseOutput(String rawCommand, JavaScriptObject properties,
                            EventBus eventBus, ProcessContextServices processContext,
                            JsTransactionApplicationContext processParameters,
                            StateTransition<DesktopPlace> callback) {

        String command = rawCommand;
        if (rawCommand.contains(" ")) {
            command = rawCommand.split(" ")[0];
        }
        GWTUtils.setAttribute(properties, outputHandlerRegistry.getPropertyName(), command);
        OutputHandler service = outputHandlerRegistry.getConfigured(properties, processContext, eventBus, processParameters);

        assert service != null : "No command '" + rawCommand + "' found";

        service.prepare(rawCommand, properties, eventBus, processContext,
                processParameters, callback);
        service.execute();
    }

}
