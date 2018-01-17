package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.event.domain.annotations.CatalogField;
import com.wrupple.muba.event.domain.annotations.CatalogValue;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.worker.domain.Task;
import com.wrupple.muba.worker.domain.Workflow;

import java.util.List;

/**
 * Created by japi on 25/07/17.
 */
public class WorkflowImpl extends ServiceManifestImpl implements Workflow {

    private String outputField;

    private boolean clearOutput;
    @ForeignKey(foreignCatalog = Task.CATALOG)
    private List<Long> process;
    @CatalogField(ignore = true)
    @CatalogValue(foreignCatalog = Task.CATALOG)
    private List<Task> processValues;


    @Override
    public boolean isClearOutput() {
        return clearOutput;
    }

    public void setClearOutput(boolean clearOutput) {
        this.clearOutput = clearOutput;
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

    @Override
    public String getCatalogType() {
        return Workflow.class.getSimpleName();
    }
}
