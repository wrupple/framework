package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;
import com.wrupple.muba.event.server.domain.impl.AbstractYieldContext;
import com.wrupple.muba.worker.domain.ApplicationContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ContextBase;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by japi on 11/05/17.
 */
@Singleton
public class ApplicationContextImpl  extends ContextBase implements ApplicationContext {
    private RuntimeContext runtimeContext;
    private ApplicationState stateValue;

    private String name;
    private ListIterator<String> taskGrammar, workerOrders;
    private Task current;

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }


    @Override
    public void setName(String command) {
        this.name = command;
    }

    @Override
    public ListIterator<String> getTaskGrammar() {

        if (current == null || current != getStateValue().getTaskDescriptorValue()) {
            taskGrammar = null;
        }

        if (taskGrammar == null) {
            current = getStateValue().getTaskDescriptorValue();
            if(current.getGrammar()==null){
                taskGrammar = Collections.EMPTY_LIST.listIterator();
            }else{
                taskGrammar = current.getGrammar().listIterator();
            }
        }

        return taskGrammar;
    }

    @Override
    public ListIterator<String> getWorkerSentence() {
        if (workerOrders == null) {
            workerOrders = assertIterator(stateValue.getWorkerStateValue());
        }
        return workerOrders;
    }



    @Override
    public void setRuntimeContext(RuntimeContext requestContext) {
        Object contract = requestContext.getServiceContract();
        ApplicationState state;
        if (contract instanceof ApplicationState) {
            state = (ApplicationState) contract;

        } else {
            Intent intent = (Intent) contract;
            state = intent.getStateValue();
        }
        WorkerState container = state.getWorkerStateValue();
        if (container == null) {
            throw new IllegalStateException("No application container");
        }
        setStateValue(state);
        this.runtimeContext = requestContext;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }


    @Override
    public RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    public String getName() {
        return name;
    }


    private ListIterator<String> assertIterator(final WorkerState workerStateValue) {
        return new ListIterator<String>() {
            @Override
            public boolean hasNext() {
                return workerStateValue.getWordIndex() < workerStateValue.getSentence().size();
            }

            @Override
            public String next() {
                String r = workerStateValue.getSentence().get(Math.toIntExact(workerStateValue.getWordIndex()));
                workerStateValue.setWordIndex(workerStateValue.getWordIndex() + 1);
                return r;
            }

            @Override
            public boolean hasPrevious() {
                return workerStateValue.getWordIndex() > 0;
            }

            @Override
            public String previous() {
                String r = workerStateValue.getSentence().get(Math.toIntExact(workerStateValue.getWordIndex()));
                workerStateValue.setWordIndex(workerStateValue.getWordIndex() - 1);
                return r;
            }

            @Override
            public int nextIndex() {
                return (int) (Math.toIntExact(workerStateValue.getWordIndex()) + 1);
            }

            @Override
            public int previousIndex() {
                return (int) (Math.toIntExact(workerStateValue.getWordIndex()) - 1);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove words from worker sentence");
            }

            @Override
            public void set(String s) {
                workerStateValue.getSentence().set(Math.toIntExact(workerStateValue.getWordIndex()), s);
            }

            @Override
            public void add(String s) {
                workerStateValue.getSentence().add(s);
            }
        };
    }

    private ApplicationContext parentValue;
    private Object parent;

    @Override
    public ApplicationContext getParentValue() {
        return parentValue;
    }

    @Override
    public ApplicationContext getRootAncestor() {
        return CatalogEntryImpl.getRootAncestor(this);
    }

    @Override
    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public void setParentValue(ApplicationContext parentValue) {
        this.parentValue = parentValue;
    }
}
