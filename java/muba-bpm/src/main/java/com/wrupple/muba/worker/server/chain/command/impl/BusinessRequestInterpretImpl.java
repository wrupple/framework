package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.Intent;
import com.wrupple.muba.worker.server.chain.command.BusinessRequestInterpret;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;

/**
 * Created by japi on 16/08/17.
 */
@Singleton
public class BusinessRequestInterpretImpl implements BusinessRequestInterpret {

    protected Logger log = LogManager.getLogger(BusinessRequestInterpretImpl.class);


    private final Provider<ApplicationContext> proveedor;

    @Inject
    public BusinessRequestInterpretImpl(Provider<ApplicationContext> proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Provider<? extends ServiceContext> getProvider(RuntimeContext runtime) {
        return proveedor;
    }

    @Override
    public boolean execute(RuntimeContext thread )throws Exception {
        Intent contract = (Intent) thread.getServiceContract();
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
        context.setStateValue(state);

        setWorkingTask(state,context);



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
        List results = session.getServiceBus().fireEvent(request, session, null);

        return (ApplicationState) results.get(0);
    }

    private void setWorkingTask(ApplicationState state, ApplicationContext context) {
        Application application = state.getApplicationValue();
        List<Task> workflow = application.getProcessValues();
        List<Long> workflowKeys = application.getProcess();

        if(workflow==null){
            throw new NullPointerException("Application "+application.getDistinguishedName()+" defines no tasks to run");
        }
            if(state.getTaskDescriptorValue()==null){
                if(state.getTaskDescriptor()==null){
                    //default start at first
                    //state.setTaskDescriptorValue(workflow.get(0));
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
