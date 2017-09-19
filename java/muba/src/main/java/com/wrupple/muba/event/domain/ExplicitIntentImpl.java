package com.wrupple.muba.event.domain;

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
    private String[] handle;

    public ExplicitIntentImpl(String... tokenValues) {
        super();
        this.handle=tokenValues;
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

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }


    public void setSentence(String[] handle) {
        this.handle = handle;
    }

    @Override
    public String getCatalogType() {
        return ExplicitIntent.CATALOG;
    }

    @Override
    public <T> T getConvertedResult() {
        return (T) result;
    }

    @Override
    public String[] getSentence() {
        return handle;
    }
}
