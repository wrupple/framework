package com.wrupple.muba.desktop.domain.impl;

import com.wrupple.muba.desktop.domain.WorkerContract;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import java.util.List;

public class WorkerContractImpl extends CatalogEntryImpl implements WorkerContract {

    private List<String> sentence;
    private Long runner;
    private String rootActivity;

    @CatalogValue(foreignCatalog = Host.CATALOG)
    private Host hostValue;
    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long host;


    public WorkerContractImpl(List<String> sentence, Long runner, String rootActivity, Host hostValue) {
        super();
        this.runner=runner;
        this.sentence = sentence;
        this.hostValue=hostValue;
        this.rootActivity=rootActivity;
        setDomain(hostValue.getDomain());
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

    @Override
    public Host getHostValue() {
        return hostValue;
    }

    public void setHostValue(Host hostValue) {
        this.hostValue = hostValue;
    }

    @Override
    public Long getHost() {
        return host;
    }

    public void setHost(Long host) {
        this.host = host;
    }
}
