package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

public class WorkerContractImpl extends CatalogEntryImpl implements WorkerContract {

    private List<String> sentence;
    private Long runner;
    private String rootActivity;

    public WorkerContractImpl() {
    }

    public WorkerContractImpl(List<String> sentence, Long runner, String rootActivity) {
        this();
        this.runner=runner;
        this.sentence = sentence;
        this.rootActivity=rootActivity;
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

    @Override
    public String getRootActivity() {
        return rootActivity;
    }

    public void setRootActivity(String rootActivity) {
        this.rootActivity = rootActivity;
    }
}
