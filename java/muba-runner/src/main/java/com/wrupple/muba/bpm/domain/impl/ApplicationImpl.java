package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

import java.util.List;

public class ApplicationImpl extends WorkflowImpl implements Application {

    private Long peer;
    private String description, exit, cancel, error;
    private List<Long> dependencies;


    @ForeignKey(foreignCatalog = Workflow.WORKFLOW_CATALOG)
    private Long explicitSuccessor;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Workflow.WORKFLOW_CATALOG)
    private Workflow explicitSuccessorValue;

    @Override

    public Long getPeer() {
        return peer;
    }

    public void setPeer(Long peer) {
        this.peer = peer;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getExit() {
        return exit;
    }

    public void setExit(String exit) {
        this.exit = exit;
    }

    @Override
    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    @Override
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public List<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Long> dependencies) {
        this.dependencies = dependencies;
    }


    @Override
    public String getCatalogType() {
        return Application.CATALOG;
    }


    public Long getExplicitSuccessor() {
        return explicitSuccessor;
    }

    public void setExplicitSuccessor(Long explicitSuccessor) {
        this.explicitSuccessor = explicitSuccessor;
    }


    @Override
    public Workflow getExplicitSuccessorValue() {
        return explicitSuccessorValue;
    }

    public void setExplicitSuccessorValue(Workflow explicitSuccessorValue) {
        this.explicitSuccessorValue = explicitSuccessorValue;
    }

}
