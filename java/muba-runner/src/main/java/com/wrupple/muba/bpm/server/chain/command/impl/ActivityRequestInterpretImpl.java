package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.chain.command.*;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by japi on 11/05/17.
 */
public class ActivityRequestInterpretImpl  implements ActivityRequestInterpret {

    private final Provider<ApplicationContext> activityContextProvider;


    @Inject
    public ActivityRequestInterpretImpl(
                                        Provider<ApplicationContext> activityContextProvider){

        this.activityContextProvider=activityContextProvider;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        ApplicationContext r = activityContextProvider.get();

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
                state = new ApplicationStateImpl();
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
