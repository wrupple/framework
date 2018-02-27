package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.impl.IntentImpl;
import com.wrupple.muba.worker.server.service.CatalogRunner;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

import static com.wrupple.muba.worker.server.chain.command.SynthesizeSolutionEntry.INTROSPECTIONKEY;

@Singleton
public class CatalogRunnerImpl implements CatalogRunner {
    private final Long runnerId;
    private final FieldAccessStrategy accessStrategy;
    private final CatalogKeyServices keyServices;

    @Inject
    public CatalogRunnerImpl(@Named("com.wrupple.runner.catalog") Long runnerId, FieldAccessStrategy accessStrategy, CatalogKeyServices keyServices) {
        this.runnerId = runnerId;
        this.accessStrategy=accessStrategy;
        this.keyServices = keyServices;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        Task task = context.getStateValue().getTaskDescriptorValue();
        //FIXME we dont need to resynthesize every single field...
        List<String> userSelection = context.getStateValue().getUserSelection();
        if(userSelection!=null) {
            if(DataContract.READ_ACTION.equals(task.getName())){
                context.getStateValue().setUserSelection(null);
                if(userSelection.isEmpty()){
                    if(userSelection.size()==1){
                        return true;
                    }
                }

            }
        }


        return false;
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {

        return new VariableEligibility() {
            @Override
            public VariableDescriptor createVariable() {
                return new VariableDescriptor() {
                    @Override
                    public FieldDescriptor getField() {
                        return field;
                    }

                    @Override
                    public <T> T getConvertedResult() {
                        return (T) getResult();
                    }

                    @Override
                    public Object getResult() {
                        Instrospection introspection=assertInstrospector(context);
                        CatalogEntry result=context.getStateValue().getEntryValue();
                        try {
                            return accessStrategy.getPropertyValue(field,result,null,introspection);
                        } catch (ReflectiveOperationException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void setResult(Object o) {

                    }

                    @Override
                    public Long getRunner() {
                        return runnerId;
                    }
                };
            }
        };
    }


    private Instrospection assertInstrospector(ApplicationContext context) {
        Instrospection r = (Instrospection) context.get(INTROSPECTIONKEY);
        if(r==null){
            r = accessStrategy.newSession(context.getStateValue().getEntryValue());
            context.put(INTROSPECTIONKEY,r);
        }
        return r;
    }

    @Override
    public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) throws Exception {
        List<String> userSelection = context.getStateValue().getUserSelection();
        String key = userSelection.get(0);
        String catalogId = (String) context.getStateValue().getTaskDescriptorValue().getCatalog();
        Object id =keyServices.decodePrimaryKeyToken(key);
        CatalogReadRequestImpl request = new CatalogReadRequestImpl(id, catalogId);

        CatalogEntry result =  context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);

        context.getStateValue().setEntryValue(result);

        callback.execute(context);

        /*
        FIXME copied from DesktopWriterCommandImpl
         */


        IntentImpl intent = new IntentImpl();
        intent.setStateValue(context.getStateValue());
        WorkerState worker = context.getStateValue().getWorkerStateValue();
        intent.setDomain(worker.getDomain());
        ApplicationState state = context.getRuntimeContext().getServiceBus().fireEvent(intent, context.getRuntimeContext(), null);
        if(state==null){
            throw new NullPointerException("Business intent resulted in no application state");
        }
        if(state.getStakeHolder()==null){
            throw new NullPointerException("No one owns this application");
        }
        // TODO a trigger for application state creation handles launching the worker or broadcasting worker?



        if(result==null){
            return Command.PROCESSING_COMPLETE;
        }
        return Command.CONTINUE_PROCESSING;
    }
}
