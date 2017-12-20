package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.BusinessIntent;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Event;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;

import java.util.List;

/**
 * Created by japi on 12/08/17.
 */
public class BusinessIntentImpl extends ManagedObjectImpl implements BusinessIntent {
    @ForeignKey(
            foreignCatalog = ApplicationState.CATALOG
    )
    private Long state;
    @CatalogValue(
            foreignCatalog = ApplicationState.CATALOG
    )
    private ApplicationState stateValue;

    @CatalogField(ignore = true)
    private Workflow implicitIntentValue;
    @ForeignKey(foreignCatalog = Workflow.WORKFLOW_CATALOG)
    private Object implicitIntent;

    private Object entry;
    private CatalogEntry entryValue;
    private String catalog;
    private Object result;
    private Exception error;
    private List<String> sentence;

    @Override
    public Workflow getImplicitIntentValue() {
        return implicitIntentValue;
    }

    @Override
    public void setImplicitIntentValue(Event stateValue) {
        this.implicitIntentValue= (Workflow) stateValue;
    }

    public void setImplicitIntentValue(Workflow implicitIntentValue) {
        this.implicitIntentValue = implicitIntentValue;
    }

    @Override
    public Object getImplicitIntent() {
        return implicitIntent;
    }

    @Override
    public void setImplicitIntent(Object implicitIntent) {
        this.implicitIntent = implicitIntent;
    }

    @Override
    public String getCatalogType() {
        return BusinessIntent_CATALOG;
    }

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
    public void setStateValue(ApplicationState applicationState) {
        this.stateValue=applicationState;
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
