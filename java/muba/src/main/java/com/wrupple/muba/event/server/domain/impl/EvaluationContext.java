package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasCatalogKey;
import com.wrupple.muba.event.domain.reserved.HasResult;
import org.apache.commons.chain.Context;

import java.util.ListIterator;

public class EvaluationContext extends AbstractYieldContext implements HasResult<Object>, HasCatalogId, HasCatalogKey,ServiceContext {
    private final String interpreter;
    private final Context context;
    private final CatalogEntry subject;
    private final ContractDescriptor subjectType;
    private final FieldDescriptor evaluate;
    private final Instrospection intro;
    private Object result;
    private EvaluationContext parent;

    public EvaluationContext(ListIterator<String> sentence,String interpreter, Context context, CatalogEntry subject, ContractDescriptor subjectType, FieldDescriptor evaluate, Instrospection intro) {
        if(sentence==null){
            throw new NullPointerException("null sentence iterator");
        }
        super.wordIterator=sentence;
        this.interpreter = interpreter;
        this.context = context;
        this.subject = subject;
        this.subjectType = subjectType;
        this.evaluate = evaluate;
        this.intro = intro;

    }

    public EvaluationContext(EvaluationContext context, CatalogDescriptor foreignCatalog, FieldDescriptor targetField, CatalogEntry targetEntry) {
        this(context.wordIterator,context.interpreter,context,targetEntry,foreignCatalog,targetField,context.intro);
        this.parent=context;
    }

    public Object getEntry(){
        return getEntryValue().getId();
    }

    public void setEntry(Object value){

    }

    public String getInterpreter() {
        return interpreter;
    }

    public Context getContext() {
        return context;
    }

    public CatalogEntry getEntryValue() {
        return subject;
    }

    public ContractDescriptor getCatalog() {
        return subjectType;
    }

    public FieldDescriptor getEvaluate() {
        return evaluate;
    }

    public Instrospection getIntro() {
        return intro;
    }

    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
    }

    @Override
    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }


    @Override
    public void setCatalog(String catalog) {

    }

    @Override
    public RuntimeContext getRuntimeContext() {
        return ((ServiceContext)context).getRuntimeContext();
    }

    @Override
    public void setRuntimeContext(RuntimeContext context) {
        ((ServiceContext)context).setRuntimeContext(context);
    }
}
