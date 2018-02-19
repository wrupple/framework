package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityRequestInterpretImpl  implements ActivityRequestInterpret {

    private final Provider<ApplicationContext> activityContextProvider;
    private final ProcessManager bpm;
    private final Provider<ApplicationState> applicationStateProvider;



    @Inject
    public ActivityRequestInterpretImpl(
            Provider<ApplicationContext> activityContextProvider, ProcessManager bpm, Provider<ApplicationState> applicationStateProvider){
        this.activityContextProvider=activityContextProvider;
        this.bpm = bpm;
        this.applicationStateProvider = applicationStateProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        ApplicationContext r = activityContextProvider.get();
        ApplicationState contract = (ApplicationState) requestContext.getServiceContract();
        WorkerState container = contract.getWorkerStateValue();
        if (container == null) {
            throw new IllegalStateException("No application container");
        }
        r.setStateValue(contract);
        return r.setRuntimeContext(requestContext);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext requestContext = (RuntimeContext) ctx;
        ApplicationContext context = requestContext.getServiceContext();
        ApplicationState state = context.getStateValue();
        if(state==null){
            if(requestContext.getServiceContract() instanceof ApplicationState){
                state = (ApplicationState) requestContext.getServiceContract();
            }else{
                state = acquireContext(null, requestContext);
            }
            context.setStateValue(state);
        }


        Task request = context.getStateValue().getTaskDescriptorValue();
        if(request==null){
            request = getFirstApplicationTask(context.getStateValue());
            if(request==null){
                //FIXME this implies the service contract will always be a task descriptor
                request = (Task) requestContext.getServiceContract();
                if(request==null){
                    //TODO get task descriptor (from token¡?)
                    throw new NullPointerException("there is no task definition");
                }
                context.getStateValue().setTaskDescriptorValue(request);
            }
        }


        return CONTINUE_PROCESSING;
    }

    private Task getFirstApplicationTask(ApplicationState stateValue) {
        Application appllication = stateValue.getApplicationValue();
        List<Task> tasks = appllication.getProcessValues();
        if(tasks==null||tasks.isEmpty()){
            throw new IllegalStateException("Application "+appllication.getDistinguishedName()+" has no tasks");
        }else{
            return tasks.get(0);
        }
    }

    public ApplicationState acquireContext(Application initialState, RuntimeContext thread) throws Exception {
        ApplicationState newState = applicationStateProvider.get();
        newState.setApplicationValue(initialState);

        CatalogCreateRequestImpl createRequest = new CatalogCreateRequestImpl(newState, ApplicationState.CATALOG);

        List results = thread.getServiceBus().fireEvent(createRequest, thread, null);

        return (ApplicationState) results.get(0);
    }
}
