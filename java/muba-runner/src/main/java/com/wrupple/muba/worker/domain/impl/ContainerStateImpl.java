package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ContainerState;
import com.wrupple.muba.event.domain.impl.CatalogEntryImpl;

import javax.validation.constraints.NotNull;

public class ContainerStateImpl extends CatalogEntryImpl implements ContainerState {


    @NotNull
    private Long runner;
    private String characterEncoding, homeActivity;
    //ignore not catalog
    private Application applicationTree;

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
    public Long getRunner() {
        return runner;
    }

    public void setRunner(Long runner) {
        this.runner = runner;
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public String getHomeActivity() {
        return homeActivity;
    }

    @Override
    public void setHomeActivity(String homeActivity) {
        this.homeActivity = homeActivity;
    }

    public Application getApplicationTree() {
        return applicationTree;
    }

    @Override
    public void setApplicationTree(Application applicationTree) {
        this.applicationTree = applicationTree;
    }
}
