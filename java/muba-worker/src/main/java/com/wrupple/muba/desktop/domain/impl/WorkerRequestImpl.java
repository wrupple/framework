package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.WorkerRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

public class WorkerRequestImpl extends CatalogEntryImpl implements WorkerRequest {

    private List<String> sentence;
    private Long runner;

    public WorkerRequestImpl() {
    }

    public WorkerRequestImpl(List<String> sentence,Long runner) {
        this();
        this.runner=runner;
        this.sentence = sentence;
        setDomain(CatalogEntry.PUBLIC_ID);
    }

    @Override
    public String getCatalogType() {
        return CATALOG;
    }

    @Override
    public Object getCatalog() {
        return getCatalogType();
    }

    @Override
    public void setCatalog(String catalog) {

    }

    @Override
    public List<String> getSentence() {
        return sentence;
    }

    public void setSentence(List<String> sentence) {
        this.sentence = sentence;
    }

    @Override
    public Long getRunner() {
        return runner;
    }

    public void setRunner(Long runner) {
        this.runner = runner;
    }
}
