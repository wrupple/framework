package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.reserved.HasResult;
import org.apache.commons.chain.Context;

import java.util.ListIterator;

public class EvaluationContext extends AbstractYieldContext implements HasResult<Object>{
    private final String interpreter;
    private final Context context;
    private final CatalogEntry subject;
    private final ContractDescriptor subjectType;
    private final FieldDescriptor evaluate;
    private final Instrospection intro;
    private Object result;

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

    public String getInterpreter() {
        return interpreter;
    }

    public Context getContext() {
        return context;
    }

    public CatalogEntry getSubject() {
        return subject;
    }

    public ContractDescriptor getSubjectType() {
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
}
