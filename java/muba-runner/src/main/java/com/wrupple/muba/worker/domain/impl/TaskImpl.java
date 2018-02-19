package com.wrupple.muba.worker.domain.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionConstraintImpl;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.event.domain.TaskToolbarDescriptor;

import java.util.List;

public class TaskImpl extends CatalogActionConstraintImpl implements Task {


    private List<Long> userActions;
    private List<Long> toolbars;
    private List<TaskToolbarDescriptor> toolbarsValues;
    private List<String> grammar;
    private String distinguishedName;
    private String outputField;
    private Object stakeHolder;
    private Long exit, cancel, error;

    private Boolean keepOutput;

    @Override
    public Boolean getKeepOutput() {
        return keepOutput;
    }

    public void setKeepOutput(Boolean keepOutput) {
        this.keepOutput = keepOutput;
    }

    @Override
    public Object getStakeHolder() {
        return stakeHolder;
    }

    public void setStakeHolder(Object stakeHolder) {
        this.stakeHolder = stakeHolder;
    }

    @Override
    public List<Long> getUserActions() {
        return userActions;
    }

    public void setUserActions(List<Long> userActions) {
        this.userActions = userActions;
    }

    @Override
    public List<Long> getToolbars() {
        return toolbars;
    }

    public void setToolbars(List<Long> toolbars) {
        this.toolbars = toolbars;
    }

    @Override
    public List<TaskToolbarDescriptor> getToolbarsValues() {
        return toolbarsValues;
    }

    public void setToolbarsValues(List<TaskToolbarDescriptor> toolbarsValues) {
        this.toolbarsValues = toolbarsValues;
    }

    @Override
    public List<String> getGrammar() {
        return grammar;
    }

    public void setGrammar(List<String> grammar) {
        this.grammar = grammar;
    }

    @Override
    public String getDistinguishedName() {
        return distinguishedName;
    }

    @Override
    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
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
        return Task.CATALOG;
    }


    private String description;



    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public CatalogEntry getExplicitSuccessorValue() {
        return null;
    }


    @Override
    public Long getError() {
        return error;
    }

    public void setError(Long error) {
        this.error = error;
    }

    @Override
    public Long getCancel() {
        return cancel;
    }

    public void setCancel(Long cancel) {
        this.cancel = cancel;
    }

    @Override
    public Long getExit() {
        return exit;
    }

    public void setExit(Long exit) {
        this.exit = exit;
    }
}
