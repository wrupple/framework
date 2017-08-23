package com.wrupple.muba.bootstrap.domain;

/**
 * Created by japi on 21/08/17.
 */
public class UserEventImpl extends CatalogEntryImpl implements UserEvent {

    /**
     * it's up to the event contract to decide if the catalog is metadata to the result or the event
     */
    private String catalog;
    private Object result;
    private Object state;
    private String[] handle;

    public UserEventImpl(String... tokenValues) {
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

    @Override
    public String[] getHandle() {
        return handle;
    }

    public void setHandle(String[] handle) {
        this.handle = handle;
    }

    @Override
    public String getCatalogType() {
        return UserEvent.class.getSimpleName();
    }

    @Override
    public <T> T getConvertedResult() {
        return (T) result;
    }

    @Override
    public String[] getSentence() {
        return getHandle();
    }
}
