package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.annotations.CatalogValue;

import java.util.List;

/**
 * Created by japi on 25/07/17.
 */
public class WorkflowImpl extends ServiceManifestImpl implements Workflow {

    private Long peer;
    private String description,outputField,exit,cancel,error;
    private List<Long> dependencies;
    @ForeignKey(foreignCatalog = Workflow.CATALOG)
    private Long explicitSuccessor;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Workflow.CATALOG)
    private Workflow explicitSuccessorValue;
    private boolean clearOutput;
    @ForeignKey(foreignCatalog = Task.CATALOG)
    private List<Long> process;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Task.CATALOG)
    private List<Task> processValues;

    @Override
    public Workflow getExplicitSuccessorValue() {
        return explicitSuccessorValue;
    }

    public void setExplicitSuccessorValue(Workflow explicitSuccessorValue) {
        this.explicitSuccessorValue = explicitSuccessorValue;
    }

    @Override
    public boolean isClearOutput() {
        return clearOutput;
    }

    public void setClearOutput(boolean clearOutput) {
        this.clearOutput = clearOutput;
    }



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
    public List<Long> getProcess() {
        return process;
    }

    public <T extends Task> List<T> getProcessValues() {
        return (List<T>) processValues;
    }

    public void setProcess(List<Long> process) {
        this.process = process;
    }



    public  <T extends Task> void setProcessValues(List<T> processValues) {
        this.processValues = (List<Task>) processValues;
    }

    @Override
    public String getOutputField() {
        return outputField;
    }

    @Override
    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }

    public Long getExplicitSuccessor() {
        return explicitSuccessor;
    }

    public void setExplicitSuccessor(Long explicitSuccessor) {
        this.explicitSuccessor = explicitSuccessor;
    }
    @Override
    public String getCatalogType() {
        return Workflow.CATALOG;
    }
}