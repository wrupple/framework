package com.wrupple.muba.event.domain.impl;

import com.wrupple.muba.event.domain.Contract;
import com.wrupple.muba.event.domain.Invocation;

import java.util.Arrays;
import java.util.List;

/**
 * Created by japi on 21/08/17.
 */
public class InvocationImpl extends CatalogEntryImpl implements Invocation {

    /**
     * it's up to the event contract to decide if the catalog is metadata to the result or the event
     */
    private String catalog;
    private Contract implicitIntentValue;
    private Object implicitIntent;
    private Exception error;
    private List<String> sentence;

    public InvocationImpl() {
    }


    public InvocationImpl(String... sentence) {
        this();
        setSentence(Arrays.asList(sentence));
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }


    @Override
    public Contract getEventValue() {
        return implicitIntentValue;
    }

    @Override
    public void setEventValue(Contract stateValue) {
        this.implicitIntentValue = stateValue;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        if(sentence==null){
            throw new NullPointerException("An explicit intent must declare a sentence");
        }
        this.sentence = sentence;
    }

    @Override
    public Object getEvent() {
        return implicitIntent;
    }

    public void setEvent(Object state) {
        this.implicitIntent = state;
    }

    @Override
    public String toString() {
        return "InvocationImpl{" +
                "catalog='" + catalog + '\'' +
                ", error=" + error +
                ", sentence=" + sentence +
                '}';
    }

    @Override
    public String getCatalogType() {
        return Invocation_CATALOG;
    }
}
