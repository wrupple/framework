package com.wrupple.muba.bpm.domain.impl;

import com.wrupple.muba.event.domain.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.ApplicationItem;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;

import java.util.List;

/**
 * Created by japi on 25/07/17.
 */
public class ApplicationItemImpl extends ServiceManifestImpl implements ApplicationItem {

    private Long peer;
    private String description,outputField,exit,cancel,error;
    private List<Long> dependencies,process;

    private List<ProcessTaskDescriptor> processValues;

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

    public <T extends ProcessTaskDescriptor> List<T> getProcessValues() {
        return (List<T>) processValues;
    }

    public void setProcess(List<Long> process) {
        this.process = process;
    }



    public  <T extends ProcessTaskDescriptor> void setProcessValues(List<T> processValues) {
        this.processValues = (List<ProcessTaskDescriptor>) processValues;
    }

    @Override
    public String getOutputField() {
        return outputField;
    }

    @Override
    public void setOutputField(String outputField) {
        this.outputField = outputField;
    }
}
