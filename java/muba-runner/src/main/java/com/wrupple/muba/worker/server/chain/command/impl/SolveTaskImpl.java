package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by rarl on 11/05/17.
 */
@Singleton
public class SolveTaskImpl implements SolveTask {
    protected Logger log = LoggerFactory.getLogger(SolveTaskImpl.class);

    private final ProcessManager plugin;
    private final Callback callback;

    @Inject
    public SolveTaskImpl(ProcessManager plugin, Callback callback) {
        this.plugin = plugin;
        this.callback = callback;
    }
    @Override
    public boolean execute(Context ctx) throws Exception {
        ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();
        CatalogDescriptor catalog = state.getCatalogValue();
        Task task = state.getTaskDescriptorValue();

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



            /*
             * Runner INTERACTION REQUIRED
             */

            log.info("Solving {} ", task.getDistinguishedName());

            if (plugin.getSolver().solve(context, callback) == CONTINUE_PROCESSING) {
                //NEVER write code HERE: call back mechanism does not act as expected on sync threads
                return CONTINUE_PROCESSING;
            } else {
                throw new IllegalStateException("No viable solution found for problem");
            }





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
