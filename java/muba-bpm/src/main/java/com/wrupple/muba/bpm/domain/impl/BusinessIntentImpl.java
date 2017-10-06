package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.BusinessIntent;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.event.domain.annotations.CatalogKey;
import com.wrupple.muba.event.domain.annotations.CatalogValue;

import java.util.List;

/**
 * Created by japi on 12/08/17.
 */
public class BusinessIntentImpl extends ManagedObjectImpl implements BusinessIntent {
    @CatalogKey(
            foreignCatalog = ApplicationState.CATALOG
    )
    private Long state;
    @CatalogValue(
            foreignCatalog = ApplicationState.CATALOG
    )
    private ApplicationState stateValue;
    @CatalogValue(
            foreignCatalog = Workflow.CATALOG
    )
    private Workflow handleValue;
    @CatalogKey(
            foreignCatalog = Workflow.CATALOG
    )
    private Long handle;
    private Object entry;
    private CatalogEntry entryValue;
    private String catalog;
    private Object result;
    private Exception error;
    private List<String> sentence;

    public Long getState() {
        return state;
    }

    @Override
    public Object getEntry() {
        return entry;
    }

    @Override
    public void setEntry(Object entry) {
        this.entry = entry;
    }

    @Override
    public CatalogEntry getEntryValue() {
        return entryValue;
    }

    public void setEntryValue(CatalogEntry entryValue) {
        this.entryValue = entryValue;
    }

    public void setState(Long state) {
        this.state = state;
    }

    @Override
    public ApplicationState getStateValue() {
        return stateValue;
    }

    @Override
    public void setStateValue(CatalogEntry applicationState) {
        setStateValue((ApplicationState)applicationState);
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

    public void setStateValue(ApplicationState stateValue) {
        this.stateValue = stateValue;
    }

    public Workflow getHandleValue() {
        return handleValue;
    }

    public void setHandleValue(Workflow handleValue) {
        this.handleValue = handleValue;
    }

    public Long getHandle() {
        return handle;
    }

    public void setHandle(Long handle) {
        this.handle = handle;
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
}
