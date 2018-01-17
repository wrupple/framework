package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.domain.Task;
import com.wrupple.muba.worker.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityRequestInterpretImpl  implements ActivityRequestInterpret {

    private final Provider<ApplicationContext> activityContextProvider;
    private final ProcessManager bpm;


    @Inject
    public ActivityRequestInterpretImpl(
            Provider<ApplicationContext> activityContextProvider, ProcessManager bpm){
        this.activityContextProvider=activityContextProvider;
        this.bpm = bpm;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        ApplicationContext r = activityContextProvider.get();
        r.setStateValue((ApplicationState) requestContext.getServiceContract());
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
                state = bpm.acquireContext(null,requestContext.getSession());
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
}
