package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.impl.CatalogOperand;
import com.wrupple.muba.event.domain.impl.CatalogReadRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.impl.IntentImpl;
import com.wrupple.muba.worker.server.service.CatalogRunner;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static com.wrupple.muba.worker.server.chain.command.SynthesizeSolutionEntry.INTROSPECTIONKEY;

@Singleton
public class CatalogRunnerImpl implements CatalogRunner {
    protected Logger log = LogManager.getLogger(CatalogRunnerImpl.class);

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
        //catalog runner handles entities

        return false;
    }

    private List<String> findTaskGrammarKey( ApplicationContext context,ApplicationState state, CatalogDescriptor catalog) {
        ListIterator<String> grammar = context.getTaskGrammar();
            if(grammar.hasNext()){
                if(grammar.next().equals(CatalogActionRequest.ENTRY_ID_FIELD)){
                    ListIterator<String> sentence = context.getWorkerSentence();
                    if(sentence.hasNext()){
                        return Collections.singletonList(sentence.next());
                    }
                }else {
                    grammar.previous();
                }
            }
        return null;
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
                    public boolean isSolved() {
                        return context.getStateValue().getEntryValue()!=null;
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
         copied from DesktopWriterCommandImpl
         */


        IntentImpl intent = new IntentImpl();
        intent.setStateValue(context.getStateValue());
        WorkerState worker = context.getStateValue().getWorkerStateValue();
        intent.setDomain(worker.getDomain());
        context.getRuntimeContext().getServiceBus().fireEvent(intent, context.getRuntimeContext(), null);

        if(result==null){
            return Command.PROCESSING_COMPLETE;
        }
        return Command.CONTINUE_PROCESSING;
    }

    @Override
    public void model(Operation result, ApplicationContext context, Instrospection intros) {
        if(result instanceof CatalogOperand){
            log.info("model catalog operand");
            CatalogOperand operation = (CatalogOperand)result;
            CatalogActionRequest request = operation.getRequest();
            if(request.getResults()==null){
                try {
                    context.getRuntimeContext().getServiceBus().fireEvent(request,context.getRuntimeContext(),null);
                } catch (Exception e) {
                    throw new RuntimeException("While modeling "+operation.getTargetField().getDistinguishedName()+" as an operation",e);
                }
            }else{
                log.info("catalog's work is already done.");
            }
        }else{
            log.trace("Catalog runner skips modeling non catalog related operation");
        }

    }

    @Override
    public void prepare(ApplicationContext context) {
        ApplicationState state = context.getStateValue();
        Task task = state.getTaskDescriptorValue();
        CatalogDescriptor catalog = context.getStateValue().getCatalogValue();

        String saveTo = task.getOutputField();
        List<String> userSelection = state.getUserSelection();

        if(userSelection==null){
            if (saveTo == null) {
                userSelection= findTaskGrammarKey(context,state, catalog);
            }else{
                Object savedData = context.get(saveTo);

                if (savedData == null) {
                    userSelection= findTaskGrammarKey(context,state, catalog);
                    if(userSelection==null){
                        HasAccesablePropertyValues params = state.getWorkerStateValue().getParametersValue();
                        if(params!=null){
                            userSelection = (List<String> ) params.getPropertyValue(saveTo);
                            if (userSelection != null) {
                                if (userSelection.isEmpty()) {
                                    userSelection = null;
                                }
                            }
                        }

                    }


                }
            }
            state.setUserSelection(userSelection);
        }
        if(userSelection!=null&&log.isDebugEnabled()){
            log.debug("User defined solution: {}",userSelection);
        }
    }

}
