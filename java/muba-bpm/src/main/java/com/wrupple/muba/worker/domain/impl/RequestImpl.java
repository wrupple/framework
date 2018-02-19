package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.worker.domain.Request;

import java.util.List;

/**
 * Created by japi on 12/08/17.
 */
public class RequestImpl extends ManagedObjectImpl implements Request {

    @CatalogField(ignore = true)
    private Object result;
    @CatalogField(ignore = true)
    private Exception error;
    private List<String> sentence;

    private String catalog;

    @CatalogField(ignore = true)
    private Workflow implicitIntentValue;
    @ForeignKey(foreignCatalog = Application.CATALOG)
    private Object implicitIntent;

    @Override
    public String getCatalogType() {
        return Request.CATALOG;
    }



    @Override
    public Workflow getEventValue() {
        return implicitIntentValue;
    }

    @Override
    public void setEventValue(Contract stateValue) {
        this.implicitIntentValue= (Workflow) stateValue;
    }

    public void setEventValue(Workflow implicitIntentValue) {
        this.implicitIntentValue = implicitIntentValue;
    }

    @Override
    public Object getEvent() {
        return implicitIntent;
    }

    @Override
    public void setEvent(Object implicitIntent) {
        this.implicitIntent = implicitIntent;
    }


    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
    }


    public Exception getError() {
        return error;
    }

    @Override
    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }


    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }
}


