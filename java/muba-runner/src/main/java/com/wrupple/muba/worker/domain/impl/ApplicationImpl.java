package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ApplicationDependency;
import com.wrupple.muba.event.domain.Host;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;

import java.util.List;

public class ApplicationImpl extends WorkflowImpl implements Application {


    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = ApplicationDependency.CATALOG)
    private List<ApplicationDependency> dependenciesValues;
    @ForeignKey(foreignCatalog = ApplicationDependency.CATALOG)
    private List<Long> dependencies;

    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Host.CATALOG)
    private Host peerValue;
    @ForeignKey(foreignCatalog = Host.CATALOG)
    private Long peer;

    @ForeignKey(foreignCatalog = Application.CATALOG)
    private Long exit, cancel, error;
    private String description, requiredRole,catalog;

    @ForeignKey(foreignCatalog = Application.CATALOG)
    private Long explicitSuccessor;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Application.CATALOG)
    private Application explicitSuccessorValue;



    private String outputField;
    private Boolean keepOutput;

    @Override
    public Boolean getKeepOutput() {
        return keepOutput;
    }

    public void setKeepOutput(Boolean keepOutput) {
        this.keepOutput = keepOutput;
    }

    @Override
    public String getOutputField() {
        return outputField;
    }

    @Override
    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }

    @Override
    public List<ApplicationDependency> getDependenciesValues() {
        return dependenciesValues;
    }

    public void setDependenciesValues(List<ApplicationDependency> dependenciesValues) {
        this.dependenciesValues = dependenciesValues;
    }

    @Override
    public List<Long> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Long> dependencies) {
        this.dependencies = dependencies;
    }

    public Host getPeerValue() {
        return peerValue;
    }

    public void setPeerValue(Host peerValue) {
        this.peerValue = peerValue;
    }

    @Override
    public Long getPeer() {
        return peer;
    }

    public void setPeer(Long peer) {
        this.peer = peer;
    }

    @Override
    public Long getExit() {
        return exit;
    }

    public void setExit(Long exit) {
        this.exit = exit;
    }

    @Override
    public Long getCancel() {
        return cancel;
    }

    public void setCancel(Long cancel) {
        this.cancel = cancel;
    }

    @Override
    public Long getError() {
        return error;
    }

    public void setError(Long error) {
        this.error = error;
    }


    @Override
    public Application getExplicitSuccessorValue() {
        return explicitSuccessorValue;
    }

    public void setExplicitSuccessorValue(Application explicitSuccessorValue) {
        this.explicitSuccessorValue = explicitSuccessorValue;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public String getCatalogType() {
        return Application.CATALOG;
    }

    @Override
    public String getCatalog() {
        return catalog;
    }

    @Override
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Long getExplicitSuccessor() {
        return explicitSuccessor;
    }

    public void setExplicitSuccessor(Long explicitSuccessor) {
        this.explicitSuccessor = explicitSuccessor;
    }
}
