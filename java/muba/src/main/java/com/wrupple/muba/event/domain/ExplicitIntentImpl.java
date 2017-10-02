package com.wrupple.muba.event.domain;

import java.util.Arrays;
import java.util.List;

/**
 * Created by japi on 21/08/17.
 */
public class ExplicitIntentImpl extends CatalogEntryImpl implements ExplicitIntent {

    /**
     * it's up to the event contract to decide if the catalog is metadata to the result or the event
     */
    private String catalog;
    private Object result;
    private Object state;
    private Exception error;
    private List<String> sentence;

    public ExplicitIntentImpl() {
    }


    public ExplicitIntentImpl(String... sentence) {
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

    public Object getResult() {
        return result;
    }

    @Override
    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getState() {
        return state;
    }

    @Override
    public void setState(Object state) {
        this.state = state;
    }

    @Override
    public <T> T getConvertedResult() {
        return (T) getResult();
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
    public String getCatalogType() {
        return CATALOG;
    }
}
