package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.domain.BusinessIntent;
import com.wrupple.muba.worker.server.chain.command.BusinessRequestInterpret;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessRequestInterpretImpl implements BusinessRequestInterpret {

    protected Logger log = LoggerFactory.getLogger(BusinessRequestInterpretImpl.class);


    private final Provider<ApplicationContext> proveedor;
    private final ProcessManager bpm;

    @Inject
    public BusinessRequestInterpretImpl(Provider<ApplicationContext> proveedor, ProcessManager bpm) {
        this.proveedor = proveedor;
        this.bpm = bpm;
    }

    @Override
    public Context materializeBlankContext(RuntimeContext requestContext) {
        return proveedor.get().setRuntimeContext(requestContext);
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        RuntimeContext thread = (RuntimeContext) ctx;
        BusinessIntent contract = (BusinessIntent) thread.getServiceContract();
        ApplicationContext context = thread.getServiceContext();

        ApplicationState state = contract.getStateValue();

        if(state==null){
            Object existingApplicationStateId = contract.getState();

            if(existingApplicationStateId==null){
                throw new NullPointerException("business intent bound to no application");
            }else{

                state= requirereContext(existingApplicationStateId,thread);
            }

            contract.setStateValue(state);
        }
        setWorkingTask(state,context);


        context.setStateValue(state);

        return CONTINUE_PROCESSING;
    }


    private ApplicationState requirereContext(Object existingApplicationStateId, RuntimeContext session) throws Exception {

        //recover application state
        CatalogActionRequestImpl request = new CatalogActionRequestImpl();
        //FIXME create/read application context of the right type
        request.setCatalog(ApplicationState.CATALOG);
        request.setEntry(existingApplicationStateId);
        request.setName(CatalogActionRequest.READ_ACTION);
        request.setFollowReferences(true);
        List results = session.getEventBus().fireEvent(request, session, null);

        return (ApplicationState) results.get(0);
    }

    private void setWorkingTask(ApplicationState state, ApplicationContext context) {
        Application application = state.getApplicationValue();
        List<Task> workflow = application.getProcessValues();
        List<Long> workflowKeys = application.getProcess();

            if(state.getTaskDescriptorValue()==null){
                if(state.getTaskDescriptor()==null){
                    //default start at first
                    state.setTaskDescriptorValue(workflow.get(0));
                }else{
                    //get key index and set
                    int index = workflowKeys.indexOf(state.getTaskDescriptor());
                    if(index<0){
                        log.error("workflow contains no task with id {}",state.getTaskDescriptor());
                    }
                    state.setTaskDescriptorValue(workflow.get(index));
                }
            }else{
                //value takes precedence over key, so overwrite key
                state.setTaskDescriptor(state.getTaskDescriptorValue().getId());
                if(workflowKeys!=null){
                    if( !workflowKeys.contains(state.getTaskDescriptor())){
                        throw new IllegalArgumentException("workflow contains no task with id "+state.getTaskDescriptor());

                    }
                }else {
                    if(!workflow.contains(state.getTaskDescriptorValue())) {
                        throw new IllegalArgumentException("workflow contains no task with id " + state.getTaskDescriptor());
                    }
                }
            }


    }
}
