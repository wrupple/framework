package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.Solver;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by rarl on 11/05/17.
 */
public class DetermineSolutionFieldsDomainImpl implements DetermineSolutionFieldsDomain {

    protected Logger log = LogManager.getLogger(DetermineSolutionFieldsDomainImpl.class);

    private final ProcessManager bpm;
    private final SolveTask.Callback callback;

    @Inject
    public DetermineSolutionFieldsDomainImpl(ProcessManager bpm, SolveTask.Callback callback){
        this.bpm = bpm;
        this.callback = callback;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();
        Task task = state.getTaskDescriptorValue();

        final Solver solver = bpm.getSolver();
        log.info("Resolving variables of task: "+task.getDistinguishedName());
        CatalogDescriptor catalog = state.getCatalogValue();


        if(task.getKeepOutput()==null||!task.getKeepOutput()){
            state.setEntryValue(null);
        }
        //FIXME missing methods might be in ProblemPresenterImpl

        String saveTo = task.getOutputField();
        List<String> userSelection = null;

        if (saveTo == null) {
            userSelection= findTaskGrammarKey(state, catalog, task);
        }else{
            Object savedData = context.get(saveTo);

            if (savedData == null) {
                userSelection= findTaskGrammarKey(state, catalog, task);
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


            } else {
                log.warn("context contained task solution and delegation to runners is skipped");
                return callback.execute(context);
            }
        }
        state.setUserSelection(userSelection);



        CatalogActionRequestImpl solutionTypeInquiry = new CatalogActionRequestImpl();
        solutionTypeInquiry.setEntry(catalog.getDistinguishedName());
        solutionTypeInquiry.setCatalog(CatalogDescriptor.CATALOG_ID);
        solutionTypeInquiry.setName(DataContract.READ_ACTION);
        solutionTypeInquiry.setFollowReferences(true);

        CatalogDescriptor solutionDescriptor = context.getRuntimeContext().getServiceBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);

        context.getStateValue().setCatalogValue(solutionDescriptor);

        log.debug("Resolving problem variable names");
        List<VariableDescriptor> variables  = solutionDescriptor.getFieldsValues().stream().
                map(field -> solver.isEligible(field,context)).
                filter(e->e!=null).
                map(eligibility -> eligibility.createVariable()).
                collect(Collectors.toList());
        context.getStateValue().setSolutionVariablesValues(variables);

        return CONTINUE_PROCESSING;
    }


    private List<String> findTaskGrammarKey(ApplicationState state, CatalogDescriptor catalog, Task task) {

        List<String> grammarList = task.getGrammar();
        if(grammarList!=null){
            if(!grammarList.isEmpty()){
                if(grammarList.get(0).equals(catalog.getKeyField())){
                    ListIterator<String> sentence = assertIterator(state.getWorkerStateValue());
                    if(sentence.hasNext()){
                        ListIterator<String> grammar = assertIterator(task);
                        grammar.next();
                        return Collections.singletonList(sentence.next());
                    }
                }
            }
        }
        return null;
    }

    private ListIterator<String> assertIterator(Task task) {
        //FIXME keep iterator across application context FOR THIS TASK
        ListIterator<String> iterator = task.getGrammar().listIterator();
        return iterator;
    }

    private ListIterator<String> assertIterator(final WorkerState workerStateValue) {
        return new ListIterator<String>() {
            @Override
            public boolean hasNext() {
                return workerStateValue.getWordIndex()<workerStateValue.getSentence().size();
            }

            @Override
            public String next() {
                String r =  workerStateValue.getSentence().get(Math.toIntExact(workerStateValue.getWordIndex()));
                workerStateValue.setWordIndex(workerStateValue.getWordIndex()+1);
                return r;
            }

            @Override
            public boolean hasPrevious() {
                return workerStateValue.getWordIndex()>0;
            }

            @Override
            public String previous() {
                String r =  workerStateValue.getSentence().get(Math.toIntExact(workerStateValue.getWordIndex()));
                workerStateValue.setWordIndex(workerStateValue.getWordIndex()-1);
                return r;
            }

            @Override
            public int nextIndex() {
                return (int) (workerStateValue.getWordIndex()+1);
            }

            @Override
            public int previousIndex() {
                return (int) (workerStateValue.getWordIndex()-1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove words from worker sentence");
            }

            @Override
            public void set(String s) {
                workerStateValue.getSentence().set(Math.toIntExact(workerStateValue.getWordIndex()),s);
            }

            @Override
            public void add(String s) {
                workerStateValue.getSentence().add(s);
            }
        };
    }
}
