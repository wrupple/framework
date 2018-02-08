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
        WorkerState container = bpm.getWorker(requestContext);
        if (container == null) {
            throw new IllegalStateException("No application container");
        }
        r.setStateValue((ApplicationState) requestContext.getServiceContract());
        return r.setRuntimeContext(requestContext, container);
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
            //FIXME this implies the service contract will always be a task descriptor
            request = (Task) requestContext.getServiceContract();
            if(request==null){
                //TODO task plugin is used as a shorthand for the more verbose catalog engine

                //TODO get task descriptor (from token¡?)
                throw new NullPointerException("there is no task definition");
            }
            context.getStateValue().setTaskDescriptorValue(request);
        }


        return CONTINUE_PROCESSING;
    }

    public ApplicationState acquireContext(Workflow initialState, RuntimeContext thread) throws Exception {
        ApplicationState newState = applicationStateProvider.get();
        newState.setApplicationValue(initialState);

        CatalogCreateRequestImpl createRequest = new CatalogCreateRequestImpl(newState, ApplicationState.CATALOG);

        List results = thread.getEventBus().fireEvent(createRequest, thread, null);

        return (ApplicationState) results.get(0);
    }
}
